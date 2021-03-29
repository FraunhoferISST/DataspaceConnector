package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.MessageProcessedNotificationMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.EntityUpdateService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.MessageProcessedNotificationService;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

/**
 * This @{@link ResourceUpdateMessageHandler} handles all incoming messages that have a
 * {@link de.fraunhofer.iais.eis.ResourceUpdateMessageImpl} as part one in the multipart message.
 * This header must have the correct '@type' reference as defined in the
 * {@link ResourceUpdateMessageImpl} JsonTypeName annotation.
 */

@Component
@RequiredArgsConstructor
@SupportedMessageType(ResourceUpdateMessageImpl.class)
public class ResourceUpdateMessageHandler implements MessageHandler<ResourceUpdateMessageImpl> {

    /**
     * Class level logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceUpdateMessageHandler.class);

    /**
     * Service for the message exception handling.
     */
    private final @NonNull MessageResponseService exceptionService;

    /**
     * Service for message processing.
     */
    private final @NonNull MessageService messageService;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for updating database entities from ids object.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * Service for handling response messages.
     */
    private final @NonNull MessageProcessedNotificationService notificationService;

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message The ids request message as header.
     * @param payload The notification message payload.
     * @return The response message.
     */
    @Override
    public MessageResponse handleMessage(final ResourceUpdateMessageImpl message,
                                         final MessagePayload payload) throws RuntimeException {
        // Validate incoming message.
        try {
            messageService.validateIncomingRequestMessage(message);
        } catch (MessageEmptyException exception) {
            return exceptionService.handleMessageEmptyException(exception);
        } catch (VersionNotSupportedException exception) {
            return exceptionService.handleInfoModelNotSupportedException(exception,
                    message.getModelVersion());
        }

        // Read relevant parameters for message processing.
        final var affectedResource = MessageUtils.extractAffectedResource(message);
        final var issuerConnector = MessageUtils.extractIssuerConnector(message);
        final var messageId = MessageUtils.extractMessageId(message);

        if (affectedResource == null || affectedResource.toString().equals("")) {
            // Without an affected resource, the message processing will be aborted.
            return exceptionService.handleMissingAffectedResource(affectedResource,
                    issuerConnector, messageId);
        }

        String payloadAsString;
        try {
            // Try to read payload as string.
            payloadAsString = MessageUtils.getStreamAsString(payload);
            if (payloadAsString.equals("")) {
                return exceptionService.handleMissingPayload(affectedResource, issuerConnector,
                        messageId);
            }
        } catch (IOException exception) {
            return exceptionService.handleMessagePayloadException(exception, messageId,
                    issuerConnector);
        }

        return updateResource(payloadAsString, affectedResource, issuerConnector, messageId);
    }

    /**
     * Update resource in internal database.
     *
     * @param payload          The payload as string.
     * @param affectedResource The affected resource.
     * @param issuerConnector  The issuer connector.
     * @param messageId        The message id.
     * @return A message response.
     */
    private MessageResponse updateResource(final String payload,
                                           final URI affectedResource,
                                           final URI issuerConnector,
                                           final URI messageId) {
        // Get ids resource from payload.
        Resource idsResource;
        try {
            idsResource = deserializationService.getResource(payload);
            final var resourceId = idsResource.getId();

            // Check if the resource id and affected resource id match.
            if (!resourceId.equals(affectedResource)) {
                return exceptionService.handleInvalidAffectedResource(resourceId,
                        affectedResource, issuerConnector, messageId);
            }
        } catch (IllegalArgumentException exception) {
            return exceptionService.handleIllegalArgumentException(exception, payload,
                    issuerConnector, messageId);
        }

        // Update requested resource with received information.
        try {
            updateService.updateResource(idsResource);
            final var idsRepresentations = idsResource.getRepresentation();
            for (final var representation : idsRepresentations) {
                updateService.updateRepresentation(representation);

                final var idsArtifacts = representation.getInstance();
                for (final var artifact : idsArtifacts) {
                    updateService.updateArtifact((Artifact) artifact);

                    // TODO Send artifact request if boolean is true.
                }
            }
        } catch (Exception exception) {
            // As the message has been received, respond with message processed notification
            // message, although saving the resource failed.
            LOGGER.warn("Updating entities failed. [resource=({})]", idsResource);
            final var statement = "Message received but resource not updated.";
            return respondToMessage(statement, issuerConnector, messageId);
        }

        // If everything has been saved.
        final var statement = "Message received and resource updated.";
        return respondToMessage(statement, issuerConnector, messageId);
    }

    /**
     * Build and send response message.
     *
     * @param message         The message indicating whether resource could be updated or not.
     * @param issuerConnector The issuer connector.
     * @param messageId       The message id.
     * @return A message response.
     */
    private MessageResponse respondToMessage(final String message,
                                             final URI issuerConnector,
                                             final URI messageId) {
        try {
            // Build ids response message.
            final var desc = new MessageProcessedNotificationMessageDesc(messageId);
            desc.setRecipient(issuerConnector);
            final var header = notificationService.buildMessage(desc);

            // Send ids response message.
            return BodyResponse.create(header, message);
        } catch (MessageBuilderException | ConstraintViolationException exception) {
            return exceptionService.handleResponseMessageBuilderException(exception,
                    issuerConnector, messageId);
        }
    }
}
