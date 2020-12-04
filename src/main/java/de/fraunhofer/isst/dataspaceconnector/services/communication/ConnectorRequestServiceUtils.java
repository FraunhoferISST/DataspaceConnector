package de.fraunhofer.isst.dataspaceconnector.services.communication;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.model.BackendSource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.UUIDUtils;
import de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceService;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import de.fraunhofer.isst.ids.framework.util.MultipartStringParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * This class handles received message content and saves the metadata and data to the internal database.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Service
public class ConnectorRequestServiceUtils {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(ConnectorRequestServiceUtils.class);

    private RequestedResourceService requestedResourceService;
    private SerializerProvider serializerProvider;

    @Autowired
    /**
     * <p>Constructor for ConnectorRequestServiceUtils.</p>
     *
     * @param requestedResourceService a {@link de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceService} object.
     * @param serializerProvider a {@link de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider} object.
     */
    public ConnectorRequestServiceUtils(RequestedResourceService requestedResourceService, SerializerProvider serializerProvider) {
        this.requestedResourceService = requestedResourceService;
        this.serializerProvider = serializerProvider;
    }

    /**
     * Saves the metadata in the internal database.
     *
     * @param response The data resource as string.
     * @return The UUID of the created resource.
     * @throws java.lang.Exception if any.
     */
    public UUID saveMetadata(String response) throws Exception {
        Map<String, String> map = MultipartStringParser.stringToMultipart(response);
        String header = map.get("header");
        String payload = map.get("payload");

        try {
            serializerProvider.getSerializer().deserialize(header, DescriptionResponseMessage.class);
//            ObjectMapper mapper = new ObjectMapper();
//            ResourceMetadata resourceMetadata = mapper.readValue(payload, ResourceMetadata.class);

            Resource resource = serializerProvider.getSerializer().deserialize(payload, ResourceImpl.class);
            return requestedResourceService.addResource(deserializeMetadata(resource));
        } catch (Exception e) {
            throw new Exception("Metadata could not be saved: " + e.getMessage());
        }
    }

    /**
     * Saves the data string in the internal database.
     *
     * @param response The data resource as string.
     * @param resourceId The resource uuid.
     * @throws java.lang.Exception if any.
     */
    public void saveData(String response, UUID resourceId) throws Exception {
        Map<String, String> map = MultipartStringParser.stringToMultipart(response);
        String header = map.get("header");
        String payload = map.get("payload");

        try {
            serializerProvider.getSerializer().deserialize(header, ArtifactResponseMessage.class);
        } catch (Exception e) {
            throw new Exception("Rejection Message received: " + payload);
        }

        try {
            requestedResourceService.addData(resourceId, payload);
        } catch (Exception e) {
            throw new Exception("Data could not be saved: " + e.getMessage());
        }
    }

    /**
     * <p>resourceExists.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @return a boolean.
     */
    public boolean resourceExists(UUID resourceId) {
        try {
            return requestedResourceService.getResource(resourceId) != null;
        } catch (InvalidResourceException e) {
            return true;
        }
    }

    private ResourceMetadata deserializeMetadata(Resource resource) {
        List<String> keywords = new ArrayList<>();
        for(TypedLiteral t : resource.getKeyword()) {
            keywords.add(t.getValue());
        }

        var representations = new HashMap<UUID, ResourceRepresentation>();
        for(Representation r : resource.getRepresentation()) {
            Artifact artifact = (Artifact) r.getInstance().get(0);
            ResourceRepresentation representation = new ResourceRepresentation(
                    UUIDUtils.createUUID((UUID x) -> representations.get(x) != null),
                    r.getMediaType().getFilenameExtension(),
                    artifact.getByteSize().intValue(),
                    artifact.getFileName(),
                    new BackendSource(BackendSource.Type.LOCAL, null, null, null)
            );

            representations.put(representation.getUuid(), representation);
        }

        return new ResourceMetadata(
                resource.getTitle().get(0).getValue(),
                resource.getDescription().get(0).getValue(),
                keywords,
                resource.getContractOffer().get(0).toRdf(),
                resource.getPublisher(),
                resource.getStandardLicense(),
                resource.getVersion(),
                representations
        );
    }
}
