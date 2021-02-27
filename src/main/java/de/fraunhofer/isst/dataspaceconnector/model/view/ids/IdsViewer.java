package de.fraunhofer.isst.dataspaceconnector.model.view.ids;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractOfferBuilder;
import de.fraunhofer.iais.eis.IANAMediaTypeBuilder;
import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.EndpointService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

// TODO cleaner split between IdsViewer and IdsResourceService
@Service
public final class IdsViewer {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdsViewer.class);

    /**
     * Service for resolving endpoints.
     */
    private final EndpointService endpointService;

    // NOTE: Not happy with the serialization happening here
    /**
     * Provides serialization of contracts.
     */
    private final SerializerProvider serializerProvider;

    /**
     * Contains the current configuration of the connnector.
     */
    private final ConfigurationContainer configContainer;

    /**
     * Constructor.
     *
     * @param endpointService The service for resolving endpoints.
     * @param serializerProvider The provider for serializer.
     * @param configContainer The container providing the configurations.
     */
    @Autowired
    public IdsViewer(final EndpointService endpointService,
            final SerializerProvider serializerProvider,
            final ConfigurationContainer configContainer) {
        this.endpointService = endpointService;
        this.serializerProvider = serializerProvider;
        this.configContainer = configContainer;
    }

    public Resource create(final de.fraunhofer.isst.dataspaceconnector.model.Resource resource) {
        final var endpoints = endpointService.getByEntity(resource.getId());
        final var endpointUri = URI.create("https://w3id.org/idsa/autogen/resource/"
                + ((EndpointId) endpoints.toArray()[0]).getResourceId());

        final var contracts = CompletableFuture.supplyAsync(
                () -> batchCreateContract(resource.getContracts()));
        final var keywords = CompletableFuture.supplyAsync(() -> getKeywords(resource));
        final var representations = CompletableFuture.supplyAsync(
                () -> batchCreateRepresentation(resource.getRepresentations()));

        final var language = resource.getLanguage();

        Resource output;
        try {
            output = new ResourceBuilder(endpointUri)
                             ._contractOffer_((ArrayList<? extends ContractOffer>) contracts.get())
                             ._created_(getGregorianOf(resource.getCreationDate()))
                             ._description_(Util.asList(
                                     new TypedLiteral(resource.getDescription(), language)))
                             ._keyword_((ArrayList<? extends TypedLiteral>) keywords.get())
                             ._language_(Util.asList(Language.EN)) // TODO parse language
                             ._modified_(getGregorianOf(resource.getModificationDate()))
                             ._publisher_(resource.getPublisher())
                             ._representation_(
                                     (ArrayList<? extends Representation>) representations.get())
                             // ._resourceEndpoint_(Util.asList(ce)) // TODO add resource endpoints
                             ._standardLicense_(resource.getLicence())
                             ._title_(Util.asList(new TypedLiteral(resource.getTitle(), language)))
                             ._version_(String.valueOf(resource.getVersion()))
                             .build();
        } catch (InterruptedException | ExecutionException exception) {
            LOGGER.warn("Failed to build resource. [exception=({})]", exception.getMessage());
            output = null;
        }

        return output;
    }

    public Representation create(
            final de.fraunhofer.isst.dataspaceconnector.model.Representation representation) {
        final var endpoints = endpointService.getByEntity(representation.getId());
        final var endpointUri = URI.create("https://w3id.org/idsa/autogen/representation/"
                + ((EndpointId) endpoints.toArray()[0]).getResourceId());

        final var artifacts = CompletableFuture.supplyAsync(
                () -> batchCreateArtifact(representation.getArtifacts()));

        Representation output;
        try {
            output =
                    new RepresentationBuilder(endpointUri)
                            ._language_(Language.EN) // TODO parse the language
                            ._mediaType_(new IANAMediaTypeBuilder()
                                                 ._filenameExtension_(representation.getMediaType())
                                                 .build())
                            ._instance_((ArrayList<? extends Artifact>) artifacts.get())
                            .build();
        } catch (InterruptedException | ExecutionException exception) {
            LOGGER.warn("Failed to build representation. [exception=({})]", exception.getMessage());
            output = null;
        }

        return output;
    }

    // NOTE: naming differently since eis.Representation and eis.Artifact produce same signature
    public List<Representation> batchCreateRepresentation(
            final Collection<de.fraunhofer.isst.dataspaceconnector.model.Representation>
                    representations) {
        return representations.parallelStream()
                .map(this::create)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Artifact create(final de.fraunhofer.isst.dataspaceconnector.model.Artifact artifact) {
        final var endpoints = endpointService.getByEntity(artifact.getId());
        final var endpointUri = URI.create("https://w3id.org/idsa/autogen/artifact/"
                + ((EndpointId) endpoints.toArray()[0]).getResourceId());

        Artifact output;
        try {
            output = new ArtifactBuilder(endpointUri)
                             ._byteSize_(BigInteger.ONE) // TODO get the real file size
                             ._fileName_(artifact.getTitle())
                             .build();
        } catch (Exception exception) {
            LOGGER.warn("Failed to build artifact. [exception=({})]", exception.getMessage());
            output = null;
        }

        return output;
    }

    public List<Artifact> batchCreateArtifact(
            final Collection<de.fraunhofer.isst.dataspaceconnector.model.Artifact> artifacts) {
        return artifacts.parallelStream()
                .map(this::create)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public ContractOffer create(
            final de.fraunhofer.isst.dataspaceconnector.model.Contract contract) {
        // At the moment the contract is stored in the first rule
        final var rule = (ContractRule) contract.getRules().toArray()[0];

        // Add the provider to the contract offer.
        try {
            final var contractOffer = serializerProvider.getSerializer().deserialize(
                    rule.getValue(), ContractOffer.class);
            return new ContractOfferBuilder()
                    ._permission_(contractOffer.getPermission())
                    ._prohibition_(contractOffer.getProhibition())
                    ._obligation_(contractOffer.getObligation())
                    ._contractStart_(contractOffer.getContractStart())
                    ._contractDate_(contractOffer.getContractDate())
                    ._consumer_(contractOffer.getConsumer())
                    ._provider_(configContainer.getConnector().getId())
                    ._contractEnd_(contractOffer.getContractEnd())
                    ._contractAnnex_(contractOffer.getContractAnnex())
                    ._contractDocument_(contractOffer.getContractDocument())
                    .build();
        } catch (IOException exception) {
            LOGGER.debug("Could not deserialize contract. [exception=({}), contract=({})]",
                    rule.getValue(), exception.getMessage());
            throw new RuntimeException("Could not deserialize contract.", exception);
        }
    }

    public List<ContractOffer> batchCreateContract(
            final Collection<de.fraunhofer.isst.dataspaceconnector.model.Contract> contracts) {
        return contracts.parallelStream().map(this::create).collect(Collectors.toList());
    }

    private static List<TypedLiteral> getKeywords(
            final de.fraunhofer.isst.dataspaceconnector.model.Resource resource) {
        final var keywords = new ArrayList<TypedLiteral>();
        for (final var keyword : resource.getKeywords()) {
            keywords.add(new TypedLiteral(keyword, resource.getLanguage()));
        }

        return keywords;
    }

    /**
     * Converts a date to XMLGregorianCalendar format.
     *
     * @param date the date object.
     * @return the XMLGregorianCalendar object or null.
     */
    private XMLGregorianCalendar getGregorianOf(final Date date) {
        final var calendar = new GregorianCalendar();
        calendar.setTime(date);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        } catch (DatatypeConfigurationException exception) {
            // Rethrow but do not register in function header
            throw new RuntimeException(exception);
        }
    }
}
