package de.fraunhofer.isst.dataspaceconnector.services.messages.implementation;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.handler.ResourceUpdateMessageHandler;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
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
    private final ResourceService requestedResourceService;
    private final ArtifactMessageService artifactMessageService;
    private URI recipient, resourceID, correlationMessageId;

    /**
     * Constructor
     *
     * @param tokenProvider The service for providing tokens
     * @param idsHttpService The service for ids messaging
     * @param configurationContainer The container with the configuration
     * @param resourceService The service for resources
     * @param serializerProvider The service for serializing
     * @param requestedResourceService The requested resource service for managing requested resources
     * @throws IllegalArgumentException if any of the parameters is null
     */
    @Autowired
    public ResourceUpdateMessageService(DapsTokenProvider tokenProvider,
                                        IDSHttpService idsHttpService,
                                        ConfigurationContainer configurationContainer,
                                        OfferedResourceServiceImpl resourceService,
                                        SerializerProvider serializerProvider,
                                        RequestedResourceServiceImpl requestedResourceService,
                                        ArtifactMessageService artifactMessageService) throws IllegalArgumentException {
        super(idsHttpService, serializerProvider, resourceService, configurationContainer);

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
     * @throws ResourceException if metadata or data could not be updated.
     * @throws MessageException if the artifact request has not been successful.
     */
    public boolean updateResource(Resource remoteResource) throws ResourceException, MessageException {
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
                // Update metadata.
                resource.setResourceMetadata(metadata);
                // Update data.
                updateArtifact(resource);
            }
            return true;
        } catch (MessageException exception) {
            LOGGER.warn("Failed to send or process new artifact request. [exception=({})]",
                    exception.getMessage());
            throw new MessageException("Data could not be updated.");
        } catch (ResourceException exception) {
            LOGGER.warn("Failed to update the data. [exception=({})]", exception.getMessage());
            throw new ResourceException("Data could not be updated.");
        } catch (NullPointerException exception){
            LOGGER.warn("Resource in ResourceUpdateMessage not found. [exception=({})]", exception.getMessage());
            throw new ResourceNotFoundException("Resource in ResourceUpdateMessage not stored locally.");
        } catch (Exception exception) {
            LOGGER.warn("Unable to update resource. [exception=({})]", exception.getMessage());
            throw new ResourceException("Unable to update resource.");
        }
    }

    /**
     * Update an artifact of a resource from a remote provider.
     * TODO Add previously used query parameters (save in RequestController)
     *
     * @param resource the requested resource to which the data belongs.
     * @throws MessageException if the artifact request has not been successful.
     * @throws ResourceException if the data could not be updated.
     */
    private void updateArtifact(RequestedResource resource) throws MessageException, ResourceException {
        URI recipient = resource.getOwnerURI();
        URI artifactId = resource.getRequestedArtifact();
        URI contractAgreementId = resource.getContractAgreement();

        Map<String, String> response;
        try {
            // Send ArtifactRequestMessage.
            artifactMessageService.setRequestParameters(recipient, artifactId, contractAgreementId);
            response = artifactMessageService.sendRequestMessage("");
        } catch (MessageBuilderException exception) {
            // Failed to build the artifact request message.
            LOGGER.warn("Failed to build a request. [exception=({})]", exception.getMessage());
            throw new MessageException("Failed to build a request.");
        } catch (MessageResponseException exception) {
            // Failed to read the artifact response message.
            LOGGER.debug("Received invalid ids response. [exception=({})]", exception.getMessage());
            throw new MessageException("Received invalid ids response.");
        } catch (MessageNotSentException exception) {
            // Failed to send the artifact request message.
            LOGGER.warn("Failed to send a request. [exception=({})]", exception.getMessage());
            throw new MessageException("Failed to send a request.");
        }

        String header, payload;
        try {
            header = response.get("header");
            payload = response.get("payload");
        } catch (Exception exception) {
            // Failed to read the message parts.
            LOGGER.info("Received invalid ids response. [exception=({})]", exception.getMessage());
            throw new MessageException("Received invalid ids response in payload.");
        }

        // Get response message type.
        final var messageType = artifactMessageService.getResponseType(header);
        if (messageType != MessageService.ResponseType.ARTIFACT_RESPONSE)
            throw new MessageException("Received incorrect response type.");

        try {
            artifactMessageService.saveData(payload, resource.getUuid());
        } catch (ResourceException exception) {
            LOGGER.warn("Could not save data to database. [exception=({})]",
                    exception.getMessage());
            throw new ResourceException("Could not save data to database.");
        }
    }
}
