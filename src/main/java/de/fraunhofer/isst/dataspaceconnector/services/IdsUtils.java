package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ConnectorConfigurationException;
import de.fraunhofer.isst.dataspaceconnector.model.ConnectorResource;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import org.jetbrains.annotations.NotNull;
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
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This class provides methods to map local connector models to IDS-specific Information Model objects.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Service
public class IdsUtils {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(IdsUtils.class);

    private final ConfigurationContainer configurationContainer;
    private final SerializerProvider serializerProvider;

    /**
     * <p>Constructor for IdsUtils.</p>
     *
     * @param configurationContainer a {@link de.fraunhofer.isst.ids.framework.spring.starter.ConfigProducer} object.
     * @param serializerProvider a {@link de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider} object.
     */
    @Autowired
    public IdsUtils(@NotNull ConfigurationContainer configurationContainer,
                    @NotNull SerializerProvider serializerProvider) {
        if(configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if(serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        this.configurationContainer = configurationContainer;
        this.serializerProvider = serializerProvider;
    }

    /**
     * Gets the resource metadata as Information Model object.
     *
     * @param resource The connector resource.
     * @return The Information Model resource.
     */
    public Resource getAsResource(ConnectorResource resource) throws RuntimeException{
        final var language = getDefaultLanguage();
        final var metadata = resource.getResourceMetadata();
        if(metadata == null)
            throw new NullPointerException("The metadata cannot be null.");

        var keywords = new ArrayList<TypedLiteral>();
        if(metadata.getKeywords() != null) {
            for (var keyword : metadata.getKeywords()) {
                keywords.add(new TypedLiteral(keyword, language));
            }
        }

        var representations = new ArrayList<Representation>();
        if (metadata.getRepresentations() != null) {
            for (var representation : metadata.getRepresentations().values()) {
                try {
                    representations.add(new RepresentationBuilder(URI.create("https://w3id.org/idsa/autogen/representation/" + representation.getUuid()))
                            ._language_(Language.EN)
                            ._mediaType_(new IANAMediaTypeBuilder()
                                    ._filenameExtension_(representation.getType())
                                    .build())
                            ._instance_(Util.asList(new ArtifactBuilder(URI.create("https://w3id.org/idsa/autogen/artifact/" + representation.getUuid()))
                                    ._byteSize_(BigInteger.valueOf(representation.getByteSize()))
                                    ._fileName_(representation.getName())
                                    .build()))
                            .build());
                }catch(ConstraintViolationException exception) {
                    throw new RuntimeException("Failed to build resource representation.",
                            exception);
                }
            }
        }

        var contracts = new ArrayList<ContractOffer>();
        if (metadata.getPolicy() != null) {
            try {
                final var contract =
                        serializerProvider.getSerializer().deserialize(metadata.getPolicy(),
                                Contract.class);
                contracts.add((ContractOffer)contract);
            } catch (IOException exception) {
                LOGGER.error(String.format("Could not deserialize contract.\nContract: [%s]",
                        metadata.getPolicy()),
                        exception);
                throw new RuntimeException("Could not deserialize contract.", exception);
            }
        }

        try {
            return new ResourceBuilder(URI.create("https://w3id.org/idsa/autogen/resource/" + resource.getUuid()))
                    ._contractOffer_(contracts)
                    ._created_(getGregorianOf(resource.getCreated()))
                    ._description_(Util.asList(new TypedLiteral(metadata.getDescription(), language)))
                    ._keyword_(keywords)
                    ._language_(Util.asList(Language.EN))
                    ._modified_(getGregorianOf(resource.getModified()))
                    ._publisher_(metadata.getOwner())
                    ._representation_(representations)
                    ._resourceEndpoint_(Util.asList(configurationContainer.getConnector().getHasDefaultEndpoint()))
                    ._standardLicense_(metadata.getLicense())
                    ._title_(Util.asList(new TypedLiteral(metadata.getTitle(), language)))
                    ._version_(metadata.getVersion())
                    .build();
        }catch(ConstraintViolationException | NullPointerException exception ){
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
    private XMLGregorianCalendar getGregorianOf(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (DatatypeConfigurationException exception) {
            // Rethrow but do not regiter in function header
            throw new RuntimeException(exception);
        }
    }

    private String getDefaultLanguage() throws ConnectorConfigurationException {
        try {
            return getLanguage(0);
        }catch(IndexOutOfBoundsException exception){
            throw new ConnectorConfigurationException("No default language has been set.");
        }
    }

    private String getLanguage(int index) throws ConnectorConfigurationException, IndexOutOfBoundsException{
        try {
            final var label = configurationContainer.getConnector().getLabel();
            if(label.size() == 0)
                throw new ConnectorConfigurationException("No language has been set.");

            final var language =label.get(index).getLanguage();

            if(language.isEmpty())
                throw new ConnectorConfigurationException("No language has been set.");

            return language;
        }catch(NullPointerException exception) {
            throw new ConnectorConfigurationException("The connector language configuration could" +
                    " not be received.", exception);
        }catch(IndexOutOfBoundsException exception) {
            throw exception;
        }
    }
}
