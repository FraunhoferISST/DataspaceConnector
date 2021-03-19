package de.fraunhofer.isst.dataspaceconnector.model.view.ids;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.IANAMediaTypeBuilder;
import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.view.ArtifactViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.model.view.OfferedResourceViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.model.view.RepresentationViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdsViewer {
    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IdsViewer.class);

    /**
     * Build ids catalog from database contract and its children.
     *
     * @param catalog The catalog.
     * @return The ids catalog.
     */
    public static ResourceCatalog create(final Catalog catalog) {
        final var resources = CompletableFuture.supplyAsync(
                () -> batchCreateResource(catalog.getOfferedResources()));

        try {
            return new ResourceCatalogBuilder()
                    ._offeredResource_((ArrayList<? extends Resource>) resources.get())
                    .build();
        } catch (Exception exception) {
            LOGGER.warn("Failed to build catalog. [exception=({})]", exception.getMessage());
            return null;
        }
    }

    /**
     * Create list of ids resources.
     *
     * @param resources List of database resources.
     * @return List of ids resources.
     */
    public static List<Resource> batchCreateResource(final Collection<OfferedResource> resources) {
        return resources.parallelStream()
                .map(IdsViewer::create)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Build ids resource from database resource and its children.
     *
     * @param resource The resource.
     * @return The ids resource.
     */
    public static Resource create(
            final de.fraunhofer.isst.dataspaceconnector.model.Resource resource) {
        final var view = new OfferedResourceViewAssembler().toModel((OfferedResource) resource);
        final var uri = view.getLink("self").get().toUri();

        // Build children.
        final var contracts =
                CompletableFuture.supplyAsync(() -> batchCreateContract(resource.getContracts()));
        final var representations = CompletableFuture.supplyAsync(
                () -> batchCreateRepresentation(resource.getRepresentations()));

        // Prepare other information.
        final var created = resource.getCreationDate();
        final var description = resource.getDescription();
        final var language = resource.getLanguage();
        final var languages = IdsUtils.getLanguages(language);
        final var keywords = IdsUtils.getKeywords(resource.getKeywords(), language);
        final var license = resource.getLicence();
        final var modified = resource.getModificationDate();
        final var publisher = resource.getPublisher();
        final var title = resource.getTitle();
        final var version = resource.getVersion();

        try {
            return new ResourceBuilder(uri) // TODO add values to data model
                                            //                    ._accrualPeriodicity_()
                                            //                    ._assetRefinement_()
                                            //                    ._contentType_()
                    ._contractOffer_((ArrayList<? extends ContractOffer>) contracts.get())
                    ._created_(IdsUtils.getGregorianOf(created))
                    ._description_(Util.asList(new TypedLiteral(description, language)))
                    ._language_((ArrayList<? extends Language>) languages)
                    ._keyword_((ArrayList<? extends TypedLiteral>) keywords)
                    ._modified_(IdsUtils.getGregorianOf(modified))
                    ._publisher_(publisher)
                    ._representation_((ArrayList<? extends Representation>) representations.get())
                    //                    ._resourceEndpoint_(Util.asList(ce)) // TODO add resource
                    //                    endpoints
                    //                    ._sovereign_()
                    //                    ._spatialCoverage_()
                    //                    ._shapesGraph_()
                    ._standardLicense_(license)
                    //                    ._temporalCoverage_()
                    //                    ._temporalResolution_()
                    ._title_(Util.asList(new TypedLiteral(title, language)))
                    ._version_(String.valueOf(version))
                    .build();
        } catch (Exception exception) {
            LOGGER.warn("Failed to build resource. [exception=({})]", exception.getMessage());
            return null;
        }
    }

    // NOTE: naming differently since eis.Representation and eis.Artifact produce same signature

    /**
     * Create list of ids representations.
     *
     * @param representations List of database representations.
     * @return List of ids representations.
     */
    public static List<Representation> batchCreateRepresentation(
            final Collection<de.fraunhofer.isst.dataspaceconnector.model.Representation>
                    representations) {
        return representations.parallelStream()
                .map(IdsViewer::create)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Build ids representation from database representation and its children.
     *
     * @param representation The representation.
     * @return The ids representation.
     */
    public static Representation create(
            final de.fraunhofer.isst.dataspaceconnector.model.Representation representation) {
        final var view = new RepresentationViewAssembler().toModel(representation);
        final var uri = view.getLink("self").get().toUri();

        final var artifacts = CompletableFuture.supplyAsync(
                () -> batchCreateArtifact(representation.getArtifacts()));

        try {
            return new RepresentationBuilder(uri) // TODO add values to data model
                                                  //                    ._created_()
                    ._instance_((ArrayList<? extends Artifact>) artifacts.get())
                    //                    ._language_(Language.EN) // TODO parse the language (where
                    //                    to get from?)
                    ._mediaType_(new IANAMediaTypeBuilder()
                                         ._filenameExtension_(representation.getMediaType())
                                         .build())
                    //                    ._modified_()
                    //                    ._shapesGraph_()
                    .build();
        } catch (Exception exception) {
            LOGGER.warn("Failed to build representation. [exception=({})]", exception.getMessage());
            return null;
        }
    }

    /**
     * Create list of ids artifacts.
     *
     * @param artifacts List of database artifacts.
     * @return List of ids artifacts.
     */
    public static List<Artifact> batchCreateArtifact(
            final Collection<de.fraunhofer.isst.dataspaceconnector.model.Artifact> artifacts) {
        return artifacts.parallelStream()
                .map(IdsViewer::create)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Build ids artifact from database artifact and its children.
     *
     * @param artifact The artifact.
     * @return The ids artifact.
     */
    public static Artifact create(
            final de.fraunhofer.isst.dataspaceconnector.model.Artifact artifact) {
        final var view = new ArtifactViewAssembler().toModel(artifact);
        final var uri = view.getLink("self").get().toUri();

        try {
            return new ArtifactBuilder(uri)
                    ._byteSize_(BigInteger.ONE) // TODO get the real file size (how?)
                    //                    ._creationDate_()
                    ._fileName_(artifact.getTitle())
                    .build();
        } catch (Exception exception) {
            LOGGER.warn("Failed to build artifact. [exception=({})]", exception.getMessage());
            return null;
        }
    }

    /**
     * Create list of ids contract offers.
     *
     * @param contracts List of database contract offers.
     * @return List of ids contract offers.
     */
    public static List<ContractOffer> batchCreateContract(
            final Collection<de.fraunhofer.isst.dataspaceconnector.model.Contract> contracts) {
        return contracts.parallelStream().map(IdsViewer::create).collect(Collectors.toList());
    }

    /**
     * Build ids contract from database contract and its children.
     *
     * @param contract The contract offer.
     * @return THe contract offer.
     */
    public static ContractOffer create(
            final de.fraunhofer.isst.dataspaceconnector.model.Contract contract) {
        //        final var rules = contract.getRules();
        //
        //        // Add the provider to the contract offer.
        //        try {
        //            final var contractOffer = serializerProvider.getSerializer().deserialize(
        //                    rule.getValue(), ContractOffer.class);
        //            return new ContractOfferBuilder()
        //                    ._permission_(contractOffer.getPermission())
        //                    ._prohibition_(contractOffer.getProhibition())
        //                    ._obligation_(contractOffer.getObligation())
        //                    ._contractStart_(contractOffer.getContractStart())
        //                    ._contractDate_(contractOffer.getContractDate())
        //                    ._consumer_(contractOffer.getConsumer())
        //                    ._provider_(configContainer.getConnector().getId())
        //                    ._contractEnd_(contractOffer.getContractEnd())
        //                    ._contractAnnex_(contractOffer.getContractAnnex())
        //                    ._contractDocument_(contractOffer.getContractDocument())
        //                    .build();
        //        } catch (IOException exception) {
        //            LOGGER.debug("Could not deserialize contract. [exception=({}),
        //            contract=({})]",
        //                    rule.getValue(), exception.getMessage());
        //            throw new RuntimeException("Could not deserialize contract.", exception);
        //        }
        return null;
    }
}
