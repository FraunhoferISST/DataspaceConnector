package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.ConnectorResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private ConfigurationContainer configurationContainer;
    private SerializerProvider serializerProvider;
    private String language;

    @Autowired
    /**
     * <p>Constructor for IdsUtils.</p>
     *
     * @param configProducer a {@link de.fraunhofer.isst.ids.framework.spring.starter.ConfigProducer} object.
     * @param serializerProvider a {@link de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider} object.
     */
    public IdsUtils(ConfigurationContainer configurationContainer, SerializerProvider serializerProvider) {
        this.configurationContainer = configurationContainer;
        this.serializerProvider = serializerProvider;

        language = configurationContainer.getConnector().getLabel().get(0).getLanguage();
    }

    /**
     * Gets the resource metadata as Information Model object.
     *
     * @param resource The connector resource.
     * @return The Information Model resource.
     */
    public Resource getAsResource(ConnectorResource resource) {
        ResourceMetadata metadata = resource.getResourceMetadata();

        ArrayList<TypedLiteral> keywords = new ArrayList<>();
        if (metadata.getKeywords() != null) {
            for (String keyword : metadata.getKeywords()) {
                keywords.add(new TypedLiteral(keyword, language));
            }
        }

        ArrayList<Representation> representations = new ArrayList<>();
        if (metadata.getRepresentations() != null) {
            for (ResourceRepresentation representation : metadata.getRepresentations().values()) {
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
            }
        }

        Contract contract = null;
        if (resource.getResourceMetadata().getPolicy() != null) {
            try {
                contract = serializerProvider.getSerializer().deserialize(resource.getResourceMetadata().getPolicy(), Contract.class);
            } catch (IOException e) {
                LOGGER.error("Could not deserialize contract: " + e.getMessage());
            }
        }

        return new ResourceBuilder(URI.create("https://w3id.org/idsa/autogen/resource/" + resource.getUuid()))
                ._contractOffer_(Util.asList((ContractOffer) contract))
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
        } catch (DatatypeConfigurationException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    /**
     * Converts a string to XMLGregorianCalendar format.
     *
     * @param string The string.
     * @return The XMLGregorianCalendar object or null.
     */
    private XMLGregorianCalendar stringToDate(String string) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null;
        try {
            date = format.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getGregorianOf(date);
    }
}
