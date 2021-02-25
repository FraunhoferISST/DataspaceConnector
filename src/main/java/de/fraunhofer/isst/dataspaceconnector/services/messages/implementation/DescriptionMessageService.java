package de.fraunhofer.isst.dataspaceconnector.services.messages.implementation;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
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
import java.util.UUID;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * The service for description messages
 */
@Service
public class DescriptionMessageService extends MessageService {

    private final Logger LOGGER = LoggerFactory.getLogger(DescriptionMessageService.class);

    private final ConfigurationContainer configurationContainer;
    private final DapsTokenProvider tokenProvider;
    private final ResourceService resourceService;
    private URI recipient, resourceId, correlationMessageId;

    /**
     * Constructor
     *
     * @param tokenProvider The service for providing tokens
     * @param idsHttpService The service for ids messaging
     * @param resourceService The service for resources
     * @param configurationContainer The container with the configuration
     * @param serializerProvider The service for serializing
     * @param requestedResourceService The service for requested resources
     * @throws IllegalArgumentException if any of the parameters is null
     */
    @Autowired
    public DescriptionMessageService(DapsTokenProvider tokenProvider,
                                     IDSHttpService idsHttpService,
                                     ConfigurationContainer configurationContainer,
                                     OfferedResourceServiceImpl resourceService,
                                     SerializerProvider serializerProvider,
        RequestedResourceServiceImpl requestedResourceService) throws IllegalArgumentException {
        super(idsHttpService, serializerProvider, resourceService, configurationContainer);

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (requestedResourceService == null)
            throw new IllegalArgumentException("The ResourceService cannot be null.");

        this.configurationContainer = configurationContainer;
        this.tokenProvider = tokenProvider;
        this.resourceService = requestedResourceService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestMessage buildRequestHeader() throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        if (resourceId == null) {
            return new DescriptionRequestMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._securityToken_(tokenProvider.getDAT())
                ._recipientConnector_(Util.asList(recipient))
                .build();
        } else {
            return new DescriptionRequestMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._requestedElement_(resourceId)
                ._securityToken_(tokenProvider.getDAT())
                ._recipientConnector_(Util.asList(recipient))
                .build();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message buildResponseHeader() throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new DescriptionResponseMessageBuilder()
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
     * @param resourceId The id of the resource
     */
    public void setRequestParameters(URI recipient, URI resourceId) {
        this.recipient = recipient;
        this.resourceId = resourceId;
    }

    /**
     * Set the response parameters for the artifact message
     *
     * @param recipient The recipient of the response
     * @param correlationMessageId The id of the correlation message
     */
    public void setResponseParameters(URI recipient, URI correlationMessageId) {
        this.recipient = recipient;
        this.correlationMessageId = correlationMessageId;
    }

    /**
     * Saves the metadata to the internal database.
     *
     * @param response The data resource as string.
     * @param resourceId The id of the resource
     * @return The UUID of the created resource.
     * @throws ResourceException if any.
     * @throws InvalidResourceException If the ids object could not be deserialized.
     */
    public UUID saveMetadata(String response, URI resourceId, URI ownerURI) throws ResourceException,
        InvalidResourceException {
        Resource resource;
        try {
            resource = getSerializerProvider().getSerializer().deserialize(response, ResourceImpl.class);
        } catch (Exception e) {
            resource = findResource(response, resourceId);
        }

        ResourceMetadata metadata;
        try {
            metadata = deserializeMetadata(resource);
        } catch (Exception exception) {
            LOGGER.info("Failed to deserialize metadata. [exception=({})]", exception.getMessage());
            throw new InvalidResourceException("Metadata could not be deserialized.");
        }

        try {
            return ((RequestedResourceServiceImpl) resourceService)
                    .addResourceWithInfo(ownerURI, UUIDUtils.uuidFromUri(resourceId), metadata);
        } catch (Exception exception) {
            LOGGER.info("Failed to save metadata. [exception=({})]", exception.getMessage());
            throw new ResourceException("Metadata could not be saved to database.");
        }
    }

    /**
     * Find a resource from a connector's resource catalog.
     *
     * @param payload The message payload
     * @param resourceId The id of the resource
     * @return The resource object.
     * @throws InvalidResourceException If the payload could not be deserialized to a base connector.
     */
    private Resource findResource(String payload, URI resourceId) throws InvalidResourceException {
        Resource resource = null;
        try {
            Connector connector = getSerializerProvider().getSerializer().deserialize(payload, BaseConnector.class);
            if (connector.getResourceCatalog() != null && !connector.getResourceCatalog().isEmpty()) {
                for (Resource r : connector.getResourceCatalog().get(0).getOfferedResource()) {
                    if (r.getId().equals(resourceId)) {
                        resource = r;
                        break;
                    }
                }
            }
        } catch (Exception exception) {
            LOGGER.info("Failed to save metadata. [exception=({})]", exception.getMessage());
            throw new InvalidResourceException("Response could not be deserialized: " + payload);
        }
        return resource;
    }
}
