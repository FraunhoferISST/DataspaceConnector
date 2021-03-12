package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.DescriptionRequestMessageImpl;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.services.ConfigurationService;
import de.fraunhofer.isst.dataspaceconnector.services.EntityResolver;
import de.fraunhofer.isst.dataspaceconnector.services.IdsEntityResolver;
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
     * Service for the current connector configuration.
     */
    private final @NonNull ConfigurationService configService;

    /**
     * Service for ids connector management.
     */
    private final @NonNull IdsConnectorService connectorService;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Service for resolving ids entities.
     */
    private final @NonNull IdsEntityResolver idsEntityResolver;

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
        messageService.checkForEmptyMessage(message);
        messageService.checkForVersionSupport(message.getModelVersion());

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
     * NOTE: Exceptions are handled automatically by
     * {@link de.fraunhofer.isst.dataspaceconnector.services.messages.MessageExceptionHandler}.
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
        } catch (InvalidResourceException e) {
            LOGGER.warn("XXX");
        }


        MessageResponse response;
//        if (entity == null) {
//            // If the resource has not been found, inform and reject.
//            LOGGER.debug("Element could not be found. [id=({}), resourceId=({})]",
//                    requestedElement, messageId);
//            response = ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND, String.format(
//                    "The requested element %s could not be found.", requestedElement),
//                    configService.extractIdFromConnector(),
//                    configService.extractOutboundModelVersionFromConnector());
//        } else {
//            // If the element has been found, build and send the description.
//            final var header = messageService
//                    .buildDescriptionResponseMessage(issuerConnector, messageId);
//            final var payload = idsEntityResolver.getEntityAsIdsRdfString(entity);
//            response = BodyResponse.create(header, payload);
//        }
        return null;
    }

    /**
     * Constructs a resource catalog description message for the connector.
     * NOTE: Exceptions are handled automatically by
     * {@link de.fraunhofer.isst.dataspaceconnector.services.messages.MessageExceptionHandler}.
     *
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The message id of the incoming message.
     * @return A response message containing the resource catalog of the connector.
     */
    public MessageResponse constructConnectorSelfDescription(final URI issuerConnector,
                                                             final URI messageId) {
        final var selfDescription = connectorService.getConnectorWithOfferedResources();
        final var header = messageService.buildDescriptionResponseMessage(issuerConnector,
                messageId);
        final var payload = IdsUtils.convertConnectorToRdf(selfDescription);
        return BodyResponse.create(header, payload);
    }
}
