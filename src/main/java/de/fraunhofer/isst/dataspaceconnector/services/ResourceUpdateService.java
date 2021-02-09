package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.ArtifactMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import org.apache.http.client.utils.URIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import javax.ws.rs.ProcessingException;
import java.net.URI;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

/**
 * The Service for updating internal Resources by fetching external Resources after receiving a ResourceUpdateMessage
 */
@Service
@EnableScheduling
public class ResourceUpdateService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceUpdateService.class);

    private final LinkedList<URI> resourceUpdateList = new LinkedList<>();
    private final ResourceService resourceService;
    private final ArtifactMessageService artifactMessageService;

    /**
     * Constructor
     */
    @Autowired
    public ResourceUpdateService(RequestedResourceServiceImpl resourceService,
                                 ArtifactMessageService artifactMessageService) {
        this.resourceService = resourceService;
        this.artifactMessageService = artifactMessageService;
    }

    /**
     * Add a URI to be processed
     *
     * @param affectedResource the URI of the affected Resource
     */
    private void addItemToQueue(URI affectedResource) {
        resourceUpdateList.offer(affectedResource);
    }

    /**
     * Updates the affected Resource if possible, otherwise adds it to the processing queue
     *
     * @param affectedResource the resource which needs to be updated
     * @return true iff resource was updated, false iff if could not been updated
     */
    public boolean updateOrSchedule(URI affectedResource) {
        try{
            updateResource(affectedResource);
            return true;
        } catch (ProcessingException e) {
            LOGGER.warn("Unable to updated resource in ResourceUpdateMessage. Scheduling retries.");
            addItemToQueue(affectedResource);
            return false;
        }
    }

    /**
     * Update a single Resource
     *
     * @param affectedResource The Resource to be updated
     * @throws ProcessingException if updating did not succeed
     */
    private void updateResource(URI affectedResource) throws ProcessingException {
        try {
            LOGGER.info("resource {} updated.", affectedResource.toString());
            // TODO: pull new representation of resources
            UUID resourceUUID = UUIDUtils.uuidFromUri(affectedResource);

            LinkedList<RequestedResource> affectedResources = ((RequestedResourceServiceImpl) resourceService)
                    .getResourcesByOriginalUUID(resourceUUID);
            try {
                for (RequestedResource resource : affectedResources) {
                    // Get owner URI
                    URI ownerURI = resource.getOwnerURI();

                    // For each resource, get all representations
                    Map<UUID, ResourceRepresentation> resourceRepresentations =
                            ((RequestedResourceServiceImpl) resourceService).getAllRepresentations(resource.getUuid());
                    // Iterate over all representations and create, send ArtifactRequestMessages messages
                    for (Map.Entry<UUID, ResourceRepresentation> entry : resourceRepresentations.entrySet()) {
                        // TODO: Add a bool check here?
                        updateRepresentation(resource.getUuid(), ownerURI, entry.getKey(), entry.getValue());
                    }
                }
            } catch (NullPointerException e){
                LOGGER.warn("Resource in ResourceUpdateMessage not found.");
            }

        } catch (Exception e) {
            throw new ProcessingException("Resource update could not be processed.");
        }
    }

    /**
     * Scheduled Method to update items of the queue
     */
    @Scheduled(fixedRateString = "${scheduling.update:100}")
    public void processQueue() {
        if(!resourceUpdateList.isEmpty()) {
            try{
                updateResource(resourceUpdateList.peek());

                // Resource has been handled successfully
                resourceUpdateList.pop();
            } catch (ProcessingException e) { // Processing Error, add resource at end of queue
                resourceUpdateList.offer(resourceUpdateList.pop());
            }
        }
    }

    private boolean updateRepresentation(UUID resourceUUID, URI recipient, UUID uuid,
                                         ResourceRepresentation resourceRepresentation) {
        URI representationID = URI.create("https://w3id.org/idsa/autogen/representation/" + uuid);
        URI contractId = resourceRepresentation.getContract();

        Map<String, String> response;
        try {
            // Send ArtifactRequestMessage.
            artifactMessageService.setRequestParameters(recipient, representationID, contractId);
            response = artifactMessageService.sendRequestMessage("");
        } catch (MessageBuilderException exception) {
            // Failed to build the artifact request message.
            LOGGER.warn("Failed to build a request. [exception=({})]", exception.getMessage());
            return false;
        } catch (MessageResponseException exception) {
            // Failed to read the artifact response message.
            LOGGER.debug("Received invalid ids response. [exception=({})]", exception.getMessage());
            return false;
        } catch (MessageNotSentException exception) {
            // Failed to send the artifact request message.
            LOGGER.warn("Failed to send a request. [exception=({})]", exception.getMessage());
            return false;
        }

        String header, payload;
        try {
            header = response.get("header");
            payload = response.get("payload");
        } catch (Exception exception) {
            // Failed to read the message parts.
            LOGGER.info("Received invalid ids response. [exception=({})]", exception.getMessage());
            return false;
        }

        // Get response message type.
        final var messageType = artifactMessageService.getResponseType(header);
        if (messageType != MessageService.ResponseType.ARTIFACT_RESPONSE)
            return false;
        try {
            artifactMessageService.saveData(payload, resourceUUID);
            return true;
        } catch (ResourceException exception) {
            LOGGER.warn("Could not save data to database. [exception=({})]",
                    exception.getMessage());
            return false;
        }


    }
}
