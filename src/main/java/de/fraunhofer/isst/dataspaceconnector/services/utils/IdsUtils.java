package de.fraunhofer.isst.dataspaceconnector.services.utils;

import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractOfferImpl;
import de.fraunhofer.iais.eis.IANAMediaTypeBuilder;
import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ConnectorConfigurationException;
import de.fraunhofer.isst.dataspaceconnector.model.ConnectorResource;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides methods to map local connector models to IDS Information Model objects.
 */
@Service
public class IdsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdsUtils.class);

    private final ConfigurationContainer configurationContainer;
    private final SerializerProvider serializerProvider;

    /**
     * Constructor for IdsUtils.
     *
     * @throws IllegalArgumentException - if any of the parameters is null.
     */
    @Autowired
    public IdsUtils(ConfigurationContainer configurationContainer,
        SerializerProvider serializerProvider) throws IllegalArgumentException {
        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        this.configurationContainer = configurationContainer;
        this.serializerProvider = serializerProvider;
    }

    /**
     * Returns current IDS base connector object.
     *
     * @return The {@link de.fraunhofer.iais.eis.Connector} object from the IDS Framework.
     * @throws ConnectorConfigurationException If the connector was not found.
     */
    public Connector getConnector() throws ConnectorConfigurationException {
        final var connector = configurationContainer.getConnector();
        if (connector == null) {
            // The connector is needed for every answer and cannot be null
            throw new ConnectorConfigurationException("No connector configurated.");
        }

        return connector;
    }

    /**
     * Gets the resource metadata as Information Model object.
     *
     * @param resource The connector resource.
     * @return The Information Model resource.
     * @throws RuntimeException - if the Information Model object could not be build.
     */
    public Resource getAsResource(ConnectorResource resource) throws RuntimeException {
        final var language = getDefaultLanguage();
        final var metadata = resource.getResourceMetadata();
        if (metadata == null) {
            throw new NullPointerException("The metadata cannot be null.");
        }

        // Get the list of keywords
        var keywords = new ArrayList<TypedLiteral>();
        if (metadata.getKeywords() != null) {
            for (var keyword : metadata.getKeywords()) {
                keywords.add(new TypedLiteral(keyword, language));
            }
        }

        // Get the list of representations
        var representations = new ArrayList<Representation>();
        if (metadata.getRepresentations() != null) {
            for (var representation : metadata.getRepresentations().values()) {
                try {
                    representations.add(new RepresentationBuilder(URI.create(
                        "https://w3id.org/idsa/autogen/representation/" + representation.getUuid()))
                        ._language_(Language.EN)
                        ._mediaType_(new IANAMediaTypeBuilder()
                            ._filenameExtension_(representation.getType())
                            .build())
                        ._instance_(Util.asList(new ArtifactBuilder(URI.create(
                            "https://w3id.org/idsa/autogen/artifact/" + representation.getUuid()))
                            ._byteSize_(BigInteger.valueOf(representation.getByteSize()))
                            ._fileName_(representation.getName())
                            .build()))
                        .build());
                } catch (ConstraintViolationException exception) {
                    throw new RuntimeException("Failed to build resource representation.",
                        exception);
                }
            }
        }

        // Get the list of contracts
        var contracts = new ArrayList<ContractOffer>();
        if (metadata.getPolicy() != null) {
            try {
                final var contractOffer = (ContractOfferImpl)
                    serializerProvider.getSerializer().deserialize(metadata.getPolicy(),
                        ContractOffer.class);
                contractOffer.setProvider(getConnector().getId());
                contracts.add(contractOffer);
            } catch (IOException exception) {
                LOGGER.debug(String.format("Could not deserialize contract.\nContract: [%s]",
                    metadata.getPolicy()), exception);
                throw new RuntimeException("Could not deserialize contract.", exception);
            }
        }

        // Build the ids resource
        try {
            return new ResourceBuilder(
                URI.create("https://w3id.org/idsa/autogen/resource/" + resource.getUuid()))
                ._contractOffer_(contracts)
                ._created_(getGregorianOf(resource.getCreated()))
                ._description_(Util.asList(new TypedLiteral(metadata.getDescription(), language)))
                ._keyword_(keywords)
                ._language_(Util.asList(Language.EN))
                ._modified_(getGregorianOf(resource.getModified()))
                ._publisher_(metadata.getOwner())
                ._representation_(representations)
                ._resourceEndpoint_(
                    Util.asList(configurationContainer.getConnector().getHasDefaultEndpoint()))
                ._standardLicense_(metadata.getLicense())
                ._title_(Util.asList(new TypedLiteral(metadata.getTitle(), language)))
                ._version_(metadata.getVersion())
                .build();
        } catch (ConstraintViolationException | NullPointerException exception) {
            // The build failed or the connector is null.
            throw new RuntimeException("Failed to build information model resource.", exception);
        }
    }

    /**
     * Converts a date to XMLGregorianCalendar format.
     *
     * @param date The date object.
     * @return The XMLGregorianCalendar object or null.
     */
    public XMLGregorianCalendar getGregorianOf(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException exception) {
            // Rethrow but do not register in function header
            throw new RuntimeException(exception);
        }
    }

    /**
     * Gets the default language, which is the first set language of the connector.
     *
     * @return The default language of the connector
     * @throws ConnectorConfigurationException - if the connector is null or no language is
     *                                         configurated.
     */
    private String getDefaultLanguage() throws ConnectorConfigurationException {
        try {
            return getLanguage(0);
        } catch (IndexOutOfBoundsException exception) {
            throw new ConnectorConfigurationException("No default language has been set.");
        }
    }

    /***
     * Gets a language from the connector.
     *
     * @param index Index of the language.
     * @return The language at the passed index.
     * @throws ConnectorConfigurationException - if the connector is null or no language is set.
     * @throws IndexOutOfBoundsException - if no language could be found at the passed index.
     */
    private String getLanguage(int index)
        throws ConnectorConfigurationException, IndexOutOfBoundsException {
        try {
            final var label = configurationContainer.getConnector().getLabel();
            if (label.size() == 0) {
                throw new ConnectorConfigurationException("No language has been set.");
            }

            final var language = label.get(index).getLanguage();

            if (language.isEmpty()) {
                throw new ConnectorConfigurationException("No language has been set.");
            }

            return language;
        } catch (NullPointerException exception) {
            throw new ConnectorConfigurationException("The connector language configuration could" +
                " not be received.", exception);
        }
    }
}
