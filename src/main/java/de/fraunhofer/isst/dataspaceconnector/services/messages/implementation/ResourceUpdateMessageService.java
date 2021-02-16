package de.fraunhofer.isst.dataspaceconnector.services.messages.implementation;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.model.BackendSource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.handler.ResourceUpdateMessageHandler;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * The service for ResourceUpdateMessages messages
 */
@Service
public class ResourceUpdateMessageService extends MessageService {
    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceUpdateMessageHandler.class);

    private final ConfigurationContainer configurationContainer;
    private final DapsTokenProvider tokenProvider;
    private URI recipient, resourceID, correlationMessageId;
    private RequestedResourceServiceImpl requestedResourceService;
    private final ArtifactMessageService artifactMessageService;


    /**
     * Constructor
     *
     * @param tokenProvider The service for providing tokens
     * @param idsHttpService The service for ids messaging
     * @param configurationContainer The container with the configuration
     * @param resourceService The service for resources
     * @param idsUtils The utilities for ids messages
     * @param serializerProvider The service for serializing
     * @param requestedResourceService The requested resource service for managing requested resources
     * @throws IllegalArgumentException if any of the parameters is null
     */
    @Autowired
    public ResourceUpdateMessageService(DapsTokenProvider tokenProvider, IDSHttpService idsHttpService,
                                        ConfigurationContainer configurationContainer, OfferedResourceServiceImpl resourceService,
                                        IdsUtils idsUtils, SerializerProvider serializerProvider,
                                        RequestedResourceServiceImpl requestedResourceService,
                                        ArtifactMessageService artifactMessageService) throws IllegalArgumentException {
        super(idsHttpService, idsUtils, serializerProvider, resourceService);

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        this.configurationContainer = configurationContainer;
        this.tokenProvider = tokenProvider;
        this.requestedResourceService = requestedResourceService;
        this.artifactMessageService = artifactMessageService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message buildRequestHeader() throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new ResourceUpdateMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._affectedResource_(resourceID)
                ._senderAgent_(connector.getId())
                ._securityToken_(tokenProvider.getDAT())
                ._recipientConnector_(Util.asList(recipient))
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message buildResponseHeader() throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new MessageProcessedNotificationMessageBuilder()
                ._securityToken_(tokenProvider.getDAT())
                ._correlationMessage_(correlationMessageId)
                ._issued_(getGregorianNow())
                ._issuerConnector_(connector.getId())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._senderAgent_(connector.getId())
                ._recipientConnector_(Util.asList(recipient))
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getRecipient() {
        return recipient;
    }

    /**
     * Set the request parameters for the message
     *
     * @param recipient The recipient of the request
     * @param resourceID The resource ID in the request
     */
    public void setRequestParameters(URI recipient, URI resourceID) {

        this.recipient = recipient;
        this.resourceID = resourceID;

    }

    /**
     * Set the response parameters for the message
     *
     * @param recipient The recipient of the response
     * @param correlationMessageId The id of the correlation message
     */
    public void setResponseParameters(URI recipient, URI correlationMessageId) {
        this.recipient = recipient;
        this.correlationMessageId = correlationMessageId;
    }

    /**
     * Update a resource in the internal database.
     *
     * @param remoteResource Resource instance of provider resource
     * @throws ResourceException if any.
     * @throws InvalidResourceException If the ids object could not be deserialized.
     */
    public boolean updateResource(Resource remoteResource) throws Exception {


        ResourceMetadata metadata;
        try {
            metadata = deserializeMetadata(remoteResource);
        } catch (Exception exception) {
            LOGGER.info("Failed to deserialize metadata. [exception=({})]", exception.getMessage());
            throw new InvalidResourceException("Metadata could not be deserialized.");
        }
        LinkedList<RequestedResource> affectedResources = ((RequestedResourceServiceImpl) requestedResourceService)
                .getResourcesByOriginalUUID(UUIDUtils.uuidFromUri(remoteResource.getId()));
        try {
            for (RequestedResource resource : affectedResources) {
                // Get owner URI
                URI ownerURI = resource.getOwnerURI();
                // Update metadata
                resource.setResourceMetadata(metadata);

                // For each resource, get all representations
                Map<UUID, ResourceRepresentation> resourceRepresentations =
                        ((RequestedResourceServiceImpl) requestedResourceService).getAllRepresentations(resource.getUuid());
                // Iterate over all representations and create, send ArtifactRequestMessages messages
                for (Map.Entry<UUID, ResourceRepresentation> entry : resourceRepresentations.entrySet()) {
                    // Get
                    updateArtifact(resource, ownerURI, entry.getKey());
                }
            }
            return true;
        } catch (NullPointerException e){
            LOGGER.warn("Resource in ResourceUpdateMessage not found.");
            throw new Exception("Resource in ResourceUpdateMessage not stored locally");
        } catch (Exception e) {
            throw new Exception("Unable to update resource");
        }
    }



    /**
     * Maps a received Infomodel resource to the internal metadata model.
     *
     * @param resource The resource
     * @return the metadata object.
     */
    private ResourceMetadata deserializeMetadata(Resource resource) {
        var metadata = new ResourceMetadata();

        if (resource.getKeyword() != null) {
            List<String> keywords = new ArrayList<>();
            for (var t : resource.getKeyword()) {
                keywords.add(t.getValue());
            }
            metadata.setKeywords(keywords);
        }

        if (resource.getRepresentation() != null) {
            var representations = new HashMap<UUID, ResourceRepresentation>();
            for (Representation r : resource.getRepresentation()) {
                int byteSize = 0;
                String name = null;
                String type = null;
                if (r.getInstance() != null && !r.getInstance().isEmpty()) {
                    Artifact artifact = (Artifact) r.getInstance().get(0);
                    if (artifact.getByteSize() != null)
                        byteSize = artifact.getByteSize().intValue();
                    if (artifact.getFileName() != null)
                        name = artifact.getFileName();
                    if (r.getMediaType() != null)
                        type = r.getMediaType().getFilenameExtension();
                }

                ResourceRepresentation representation = new ResourceRepresentation(
                        UUIDUtils.uuidFromUri(r.getId()), type, byteSize, name,
                        new BackendSource(BackendSource.Type.LOCAL, null, null, null)
                );

                representations.put(representation.getUuid(), representation);
            }
            metadata.setRepresentations(representations);
        }

        if (resource.getTitle() != null && !resource.getTitle().isEmpty())
            metadata.setTitle(resource.getTitle().get(0).getValue());

        if (resource.getDescription() != null && !resource.getDescription().isEmpty())
            metadata.setDescription(resource.getDescription().get(0).getValue());

        if (resource.getContractOffer() != null && !resource.getContractOffer().isEmpty())
            metadata.setPolicy(resource.getContractOffer().get(0).toRdf());

        if (resource.getPublisher() != null)
            metadata.setOwner(resource.getPublisher());

        if (resource.getStandardLicense() != null)
            metadata.setLicense(resource.getStandardLicense());

        if (resource.getVersion() != null)
            metadata.setVersion(resource.getVersion());

        return metadata;
    }

    /**
     * Update an artifact of a resource from a remote provider.
     * @param resource the requested resource to which the artifact belongs
     * @param recipient the address of the recipient (remote connector)
     * @param uuid the UUID of the artifact to be updated
     * */
    private void updateArtifact(RequestedResource resource, URI recipient, UUID uuid) throws Exception {
        URI artifactID = URI.create("https://w3id.org/idsa/autogen/artifact/" + uuid);
        URI contractId = resource.getContractAgreement();

        Map<String, String> response;
        try {
            // Send ArtifactRequestMessage.
            artifactMessageService.setRequestParameters(recipient, artifactID, contractId);
            response = artifactMessageService.sendRequestMessage("");
        } catch (MessageBuilderException exception) {
            // Failed to build the artifact request message.
            LOGGER.warn("Failed to build a request. [exception=({})]", exception.getMessage());
            throw new Exception("Failed to build a request.");
        } catch (MessageResponseException exception) {
            // Failed to read the artifact response message.
            LOGGER.debug("Received invalid ids response. [exception=({})]", exception.getMessage());
            throw new Exception("Received invalid ids response.");
        } catch (MessageNotSentException exception) {
            // Failed to send the artifact request message.
            LOGGER.warn("Failed to send a request. [exception=({})]", exception.getMessage());
            throw new Exception("Failed to send a request.");
        }

        String header, payload;
        try {
            header = response.get("header");
            payload = response.get("payload");
        } catch (Exception exception) {
            // Failed to read the message parts.
            LOGGER.info("Received invalid ids response. [exception=({})]", exception.getMessage());
            throw new Exception("Received invalid ids response in payload.");
        }

        // Get response message type.
        final var messageType = artifactMessageService.getResponseType(header);
        if (messageType != MessageService.ResponseType.ARTIFACT_RESPONSE)
            throw new Exception("Received incorrect response type.");
        try {
            artifactMessageService.saveData(payload, resource.getUuid());
        } catch (ResourceException exception) {
            LOGGER.warn("Could not save data to database. [exception=({})]",
                    exception.getMessage());
            throw new Exception("Could not save data to database.");
        }
    }
}
