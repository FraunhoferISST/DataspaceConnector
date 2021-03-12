package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.DescriptionRequestMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.controller.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.InfoModelVersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.ResponseMessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.services.EntityResolver;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageExceptionHandler;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.ResponseMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.ids.IdsConnectorService;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
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

import java.net.URI;

/**
 * This @{@link DescriptionRequestHandler} handles all incoming messages that have a
 * {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl} as part one in the multipart
 * message. This header must have the correct '@type' reference as defined in the
 * {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl} JsonTypeName annotation.
 */
@Component
@RequiredArgsConstructor
@SupportedMessageType(DescriptionRequestMessageImpl.class)
public class DescriptionRequestHandler implements MessageHandler<DescriptionRequestMessageImpl> {

    /**
     * Class level logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(DescriptionRequestHandler.class);

    /**
     * Service for handling response messages.
     */
    private final @NonNull ResponseMessageService messageService;

    /**
     * Service for the message exception handling.
     */
    private final @NonNull MessageExceptionHandler exceptionHandler;

    /**
     * Service for ids connector management.
     */
    private final @NonNull IdsConnectorService connectorService;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message The ids request message as header.
     * @param payload The request message payload
     * @return The message response
     * @throws RuntimeException if the response body failed to be build or requestMessage is null.
     */
    @Override
    public MessageResponse handleMessage(final DescriptionRequestMessageImpl message,
                                         final MessagePayload payload) {
        // Check general message details.
        try {
            messageService.checkForEmptyMessage(message);
            messageService.checkForVersionSupport(message.getModelVersion());
        } catch (MessageEmptyException exception) {
            return exceptionHandler.handleMessageEmptyException(exception);
        } catch (InfoModelVersionNotSupportedException exception) {
            return exceptionHandler.handleInfoModelNotSupportedException(exception,
                    message.getModelVersion());
        }

        // Read relevant parameters for message processing.
        final var requestedElement = MessageUtils.extractRequestedElementFromMessage(message);
        final var issuerConnector = MessageUtils.extractIssuerConnectorFromMessage(message);
        final var messageId = MessageUtils.extractMessageIdFromMessage(message);

        MessageResponse response;
        // Check if a specific resource has been requested.
        if (requestedElement == null) {
            response = constructConnectorSelfDescription(issuerConnector, messageId);
        } else {
            response = constructResourceDescription(requestedElement, issuerConnector, messageId);
        }

        return response;
    }

    /**
     * Constructs the response message for a given resource description request message.
     *
     * @param requestedElement The requested element.
     * @param issuerConnector  The issuer connector extracted from the incoming message.
     * @param messageId        The message id of the incoming message.
     * @return The response message to the passed request.
     */
    public MessageResponse constructResourceDescription(final URI requestedElement,
                                                        final URI issuerConnector,
                                                        final URI messageId) {
        try {
            final var entity = entityResolver.getEntityById(requestedElement);

            MessageResponse response;
            if (entity == null) {
                response = exceptionHandler.handleResourceNotFoundException(requestedElement,
                        issuerConnector, messageId);
            } else {
                // If the element has been found, build and send the description.
                final var header = messageService
                        .buildDescriptionResponseMessage(issuerConnector, messageId);
                final var payload = entityResolver.getEntityAsIdsRdfString(entity);
                response = BodyResponse.create(header, payload);
            }
            return response;
        } catch (ResourceNotFoundException exception) {
            return exceptionHandler.handleResourceNotFoundException(exception, requestedElement,
                    issuerConnector, messageId);
        } catch (InvalidResourceException exception) {
            return exceptionHandler.handleInvalidResourceException(exception, requestedElement,
                    issuerConnector, messageId);
        } catch (ResponseMessageBuilderException exception) {
            return exceptionHandler.handleResponseMessageBuilderException(exception);
        } catch (IllegalStateException exception) {
            return exceptionHandler.handleIllegalStateException(exception);
        } catch (ConstraintViolationException exception) {
            return exceptionHandler.handleConstraintViolationException(exception);
        }
    }

    /**
     * Constructs a resource catalog description message for the connector.
     *
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The message id of the incoming message.
     * @return A response message containing the resource catalog of the connector.
     */
    public MessageResponse constructConnectorSelfDescription(final URI issuerConnector,
                                                             final URI messageId) {
        try {
            // Get self-description.
            final var selfDescription = connectorService.getConnectorWithOfferedResources();

            // Build ids response message.
            final var header = messageService
                    .buildDescriptionResponseMessage(issuerConnector, messageId);
            final var payload = IdsUtils.convertConnectorToRdf(selfDescription);

            // Send ids response message.
            return BodyResponse.create(header, payload);
        } catch (ConstraintViolationException exception) {
            return exceptionHandler.handleConstraintViolationException(exception);
        } catch (ResponseMessageBuilderException exception) {
            return exceptionHandler.handleResponseMessageBuilderException(exception);
        } catch (IllegalStateException exception) {
            return exceptionHandler.handleIllegalStateException(exception);
        }
    }
}
