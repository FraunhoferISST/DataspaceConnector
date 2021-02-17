package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.ResourceUpdateMessageService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * This @{@link ResourceUpdateMessageHandler} handles
 * all incoming messages that have a {@link de.fraunhofer.iais.eis.ResourceUpdateMessageImpl} as
 * part one in the multipart message. This header must have the correct '@type' reference as defined
 * in the {@link ResourceUpdateMessageImpl} JsonTypeName annotation.
 */
@Component
@SupportedMessageType(ResourceUpdateMessageImpl.class)
public class ResourceUpdateMessageHandler implements MessageHandler<ResourceUpdateMessageImpl> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceUpdateMessageHandler.class);

    private final ResourceUpdateMessageService messageService;
    private final ConfigurationContainer configurationContainer;
    private final SerializerProvider serializerProvider;

    /**
     * Constructor for ResourceUpdateMessageHandler.
     *
     * @param configurationContainer The container with the configuration
     * @param resourceUpdateMessageService The service responsible for resourceUpdateMessages
     * @throws IllegalArgumentException if one of the parameters is null.
     */
    @Autowired
    public ResourceUpdateMessageHandler(ConfigurationContainer configurationContainer,
                                        ResourceUpdateMessageService resourceUpdateMessageService,
                                        SerializerProvider serializerProvider)
            throws IllegalArgumentException {
        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (resourceUpdateMessageService == null)
            throw new IllegalArgumentException("The ResourceUpdateMessageService cannot be null.");

        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        this.configurationContainer = configurationContainer;
        this.messageService = resourceUpdateMessageService;
        this.serializerProvider = serializerProvider;

    }

    /**
     * This method handles the resource update upon receiving a ResourceUpdateMessage
     *
     * @param message        The received ResourceUpdateMessage message.
     * @param messagePayload The ResourceUpdateMessage messages content.
     * @return The response message.
     * @throws RuntimeException if the response body failed to be build.
     */
    @Override
    public MessageResponse handleMessage(ResourceUpdateMessageImpl message,
                                         MessagePayload messagePayload) throws RuntimeException {
        if (message == null) {
            LOGGER.warn("Cannot respond when there is no request.");
            throw new IllegalArgumentException("The requestMessage cannot be null.");
        }

        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        // Check if version is supported.
        if (!messageService.versionSupported(message.getModelVersion())) {
            LOGGER.debug("Information Model version of requesting connector is not supported.");
            return ErrorResponse.withDefaultHeader(
                    RejectionReason.VERSION_NOT_SUPPORTED,
                    "Information model version not supported.",
                    connector.getId(), connector.getOutboundModelVersion());
        }

        // Extract and deserialize resource
        Resource resource;
        try {
            String payload = IOUtils
                    .toString(messagePayload.getUnderlyingInputStream(), StandardCharsets.UTF_8);
            // If request is empty, return rejection message.
            if (payload.equals("")) {
                LOGGER.debug("Payload is missing [id=({}), payload=({})]", message.getId(), payload);
                return ErrorResponse
                        .withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                                "Missing resource.",
                                connector.getId(), connector.getOutboundModelVersion());
            }
            resource = serializerProvider.getSerializer().deserialize(payload, Resource.class);
        } catch (IOException exception) {
            LOGGER.debug("Cannot read payload. [id=({}), payload=({})]",
                    message.getId(), messagePayload);
            return ErrorResponse
                    .withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                            "Malformed payload.",
                            connector.getId(), connector.getOutboundModelVersion());
        }

        boolean successfulUpdate = false;
        try {
            successfulUpdate = messageService.updateResource(resource);
        } catch (ResourceException exception) {
            LOGGER.warn("Unable to update data or metadata. [exception=({})]", exception.getMessage());
        } catch (MessageException exception) {
            LOGGER.warn("Unable to receive new data. [exception=({})]", exception.getMessage());
        }

        try {
            // Build response header.
            messageService.setResponseParameters(message.getIssuerConnector(), message.getId());
            if (successfulUpdate)
                return BodyResponse.create(messageService.buildResponseHeader(),
                        "Message received and resource updated.");
            else
                return BodyResponse.create(messageService.buildResponseHeader(),
                        "Message received but resource not updated.");
        } catch (ConstraintViolationException | MessageException exception) {
            // The response could not be constructed.
            LOGGER.warn("Unable to build response message. [exception=({})]", exception.getMessage());
            return ErrorResponse.withDefaultHeader(
                    RejectionReason.INTERNAL_RECIPIENT_ERROR,
                    "Response could not be constructed.",
                    connector.getId(), connector.getOutboundModelVersion());
        }
    }
}
