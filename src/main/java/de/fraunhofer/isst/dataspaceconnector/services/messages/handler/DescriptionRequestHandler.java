package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UUIDFormatException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.DescriptionMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * This @{@link DescriptionRequestHandler} handles all
 * incoming messages that have a {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl} as
 * part one in the multipart message. This header must have the correct '@type' reference as defined
 * in the {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl} JsonTypeName annotation.
 */
@Component
@SupportedMessageType(DescriptionRequestMessageImpl.class)
public class DescriptionRequestHandler implements MessageHandler<DescriptionRequestMessageImpl> {

    public static final Logger LOGGER = LoggerFactory.getLogger(DescriptionRequestHandler.class);

    private final DescriptionMessageService messageService;
    private final ResourceService resourceService;
    private final ConfigurationContainer configurationContainer;

    /**
     * Constructor for DescriptionMessageHandler.
     *
     * @param configurationContainer  The container with the configuration
     * @param messageService The service for sending messages
     * @param offeredResourceService The service for offered resources
     * @throws IllegalArgumentException if one of the parameters is null.
     */
    @Autowired
    public DescriptionRequestHandler(ConfigurationContainer configurationContainer,
                                     DescriptionMessageService messageService, OfferedResourceServiceImpl offeredResourceService)
        throws IllegalArgumentException {
        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (messageService == null)
            throw new IllegalArgumentException("The DescriptionMessageService cannot be null.");

        if (offeredResourceService == null)
            throw new IllegalArgumentException("The OfferedResourceServiceImpl cannot be null.");

        this.messageService = messageService;
        this.resourceService = offeredResourceService;
        this.configurationContainer = configurationContainer;
    }

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param requestMessage The request message
     * @param messagePayload The request message payload
     * @return The message response
     * @throws RuntimeException if the response body failed to be build or requestMessage is null.
     */
    @Override
    public MessageResponse handleMessage(DescriptionRequestMessageImpl requestMessage,
        MessagePayload messagePayload) throws RuntimeException {
        if (requestMessage == null) {
            LOGGER.warn("Cannot respond when there is no request.");
            throw new IllegalArgumentException("The requestMessage cannot be null.");
        }

        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        // Check if version is supported.
        if (!messageService.versionSupported(requestMessage.getModelVersion())) {
            LOGGER.debug("Information Model version of requesting connector is not supported.");
            return ErrorResponse.withDefaultHeader(
                RejectionReason.VERSION_NOT_SUPPORTED,
                "Information model version not supported.",
                connector.getId(), connector.getOutboundModelVersion());
        }

        // Check if a specific resource has been requested.
        if (requestMessage.getRequestedElement() != null) {
            try {
                return constructResourceDescription(requestMessage);
            } catch (RuntimeException exception) {
                // Something went wrong (e.g invalid config), try to fix it at a higher level.
                throw new RuntimeException("Failed to construct a resource.", exception);
            }
        } else {
            // No resource has been requested, return a resource catalog.
            try {
                return constructConnectorSelfDescription(requestMessage);
            } catch (RuntimeException exception) {
                // Something went wrong (e.g invalid config), try to fix it at a higher level.
                throw new RuntimeException("Failed to construct a self-description.", exception);
            }
        }
    }

    /**
     * Constructs the response message for a given resource description request message.
     *
     * @param requestMessage The message containing the resource request.
     * @return The response message to the passed request.
     * @throws RuntimeException                - if the response message could not be constructed.
     */
    public MessageResponse constructResourceDescription(DescriptionRequestMessage requestMessage)
        throws RuntimeException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        try {
            // Find the requested resource.
            final var resourceId = UUIDUtils.uuidFromUri(requestMessage.getRequestedElement());
            final var resource = ((OfferedResourceServiceImpl) resourceService)
                .getOfferedResources().get(resourceId);

            if (resource != null) {
                // If the resource has been found, send the description.
                messageService.setResponseParameters(requestMessage.getIssuerConnector(),
                    requestMessage.getId());
                return BodyResponse.create(messageService.buildResponseHeader(),
                    resource.toRdf());
            } else {
                // If the resource has not been found, inform and reject.
                LOGGER.debug("Resource could not be found. [id=({}), resourceId=({})]",
                    resourceId, requestMessage.getId());

                return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND, String.format(
                    "The resource %s could not be found.", resourceId),
                    connector.getId(), connector.getOutboundModelVersion());
            }
        } catch (UUIDFormatException exception) {
            // If no resource uuid could be found in the request, reject the message.
            LOGGER.debug(
                "Description has no valid uuid. [id=({}), requestedElement=({}), exception=({})].",
                requestMessage.getId(), requestMessage.getRequestedElement(),
                exception.getMessage());

            return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "No valid resource id found.",
                connector.getId(), connector.getOutboundModelVersion());
        } catch (ConstraintViolationException | MessageException exception) {
            // The response could not be constructed.
            return ErrorResponse.withDefaultHeader(
                RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.",
                connector.getId(), connector.getOutboundModelVersion());
        }
    }

    /**
     * Constructs a resource catalog description message for the connector.
     *
     * @param requestMessage The request message
     * @return A response message containing the resource catalog of the connector.
     * @throws RuntimeException                - if the response message could not be constructed or
     *                                         the connector could not be serialized.
     */
    public MessageResponse constructConnectorSelfDescription(
        DescriptionRequestMessage requestMessage) throws RuntimeException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();
        try {
            // Create a connector with a list of offered resources.
            var connectorImpl = (BaseConnectorImpl) connector;
            connectorImpl.setResourceCatalog(Util.asList(new ResourceCatalogBuilder()
                ._offeredResource_(new ArrayList<>(resourceService.getResources()))
                .build()));

            // Answer with the resource description.
            messageService.setResponseParameters(requestMessage.getIssuerConnector(),
                requestMessage.getId());
            return BodyResponse.create(messageService.buildResponseHeader(),
                connectorImpl.toRdf());
        } catch (ConstraintViolationException | MessageBuilderException exception) {
            // The response could not be constructed.
            return ErrorResponse.withDefaultHeader(
                RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.",
                connector.getId(), connector.getOutboundModelVersion());
        }
    }
}
