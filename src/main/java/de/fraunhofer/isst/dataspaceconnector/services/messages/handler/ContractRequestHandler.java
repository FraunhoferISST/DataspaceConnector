package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MalformedPayloadException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MissingPayloadException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.services.EntityDependencyResolver;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageExceptionService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageProcessingService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ContractRejectionService;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This @{@link ContractRequestHandler} handles all incoming messages that have a
 * {@link de.fraunhofer.iais.eis.ContractRequestMessageImpl} as part one in the multipart message.
 * This header must have the correct '@type' reference as defined in the
 * {@link de.fraunhofer.iais.eis.ContractRequestMessageImpl} JsonTypeName annotation.
 */
@Component
@SupportedMessageType(ContractRequestMessageImpl.class)
@RequiredArgsConstructor
public class ContractRequestHandler implements MessageHandler<ContractRequestMessageImpl> {

    /**
     * Class level logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ContractRequestHandler.class);

    /**
     * Service for the message exception handling.
     */
    private final @NonNull MessageExceptionService exceptionService;

    /**
     * Service for ids deserializing.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for message processing.
     */
    private final @NonNull MessageProcessingService processingService;

    private final @NonNull ContractRejectionService rejectionService;

//    private final @NonNull ConfigurationContainer configurationContainer;
//    private final @NonNull NegotiationService negotiationService;
//    private final @NonNull PolicyDecisionService policyDecisionService;
//    private final @NonNull ResponseMessageService messageService;
//    private final @NonNull DapsTokenProvider tokenProvider;
//    private final @NonNull NotificationMessageServices logMessageService;

    /**
     *
     */
    private final @NonNull EntityDependencyResolver entityDependencyResolver;

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message The ids request message as header.
     * @param payload The request message payload.
     * @return The response message.
     */
    @Override
    public MessageResponse handleMessage(final ContractRequestMessageImpl message,
                                         final MessagePayload payload) {
        // Validate incoming message.
        try {
            processingService.validateIncomingRequestMessage(message);
        } catch (MessageEmptyException exception) {
            return exceptionService.handleMessageEmptyException(exception);
        } catch (VersionNotSupportedException exception) {
            return exceptionService.handleInfoModelNotSupportedException(exception,
                    message.getModelVersion());
        }

        // Read relevant parameters for message processing.
        final var issuerConnector = MessageUtils.extractIssuerConnectorFromMessage(message);
        final var messageId = MessageUtils.extractMessageIdFromMessage(message);

        // Read message payload as string.
        String payloadAsString;
        try {
            payloadAsString = MessageUtils.getPayloadAsString(payload);
        } catch (MalformedPayloadException exception) {
            return exceptionService.handleMalformedPayloadException(exception, messageId, issuerConnector);
        } catch (MissingPayloadException exception) {
            return exceptionService.handleMissingPayloadException(exception, messageId, issuerConnector);
        }

        // Check the contract's content.
        // return checkContractRequest(payloadAsString);
        return null;
    }



    /**
     * Checks if the contract request content by the consumer complies with the contract offer by
     * the provider.
     *
     * @param payload The message payload containing a contract request.
     * @return A message response to the requesting connector.
     */
//    public MessageResponse checkContractRequest(final String payload) throws RuntimeException {
//        try {
//            // Deserialize string to contract object.
//            final var contractRequest = deserializationService.deserializeContractRequest(payload);
//
//            final var ruleList = PolicyUtils.extractRulesFromContract(contractRequest);
//            for (final var rule : ruleList) {
//                final var targetId = rule.getTarget();
//            }
//
//            // Get artifact id from contract request.
//            final var artifactId = entityDependencyResolver.getTargetIdFromContract(contractRequest);
//
//            // Load contract offer from metadata.
//            ContractOffer contractOffer = entityDependencyResolver.getContractOfferByArtifact(UUIDUtils.uuidFromUri(artifactId));
//
//            // Check if the contract request has the same content as the stored contract offer.
//            if (negotiationService.compareContracts(contractRequest, contractOffer)) {
//                return acceptContract(contractRequest);
//            } else {
//                // If differences have been detected.
//                return rejectContract();
//            }
//        } catch (UUIDFormatException | RequestFormatException exception) {
//            LOGGER.debug(
//                    "Artifact has no valid uuid. [id=({}), artifactUri=({}), exception=({})]",
//                    requestMessage.getId(), requestMessage.getTransferContract(),
//                    exception.getMessage());
//            return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
//                    "No valid resource id found.",
//                    connector.getId(),
//                    connector.getOutboundModelVersion());
//        } catch (ResourceNotFoundException exception) {
//            // The resource could be not be found.
//            LOGGER.debug("The artifact could not be found. [id=({}), exception=({})]",
//                    requestMessage.getId(), exception.getMessage());
//            return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND,
//                    "Artifact not found.", connector.getId(),
//                    connector.getOutboundModelVersion());
//        } catch (MessageBuilderException exception) {
//            LOGGER.warn("Response could not be constructed. [id=({}), exception=({})]",
//                    requestMessage.getId(), exception.getMessage());
//            return ErrorResponse.withDefaultHeader(
//                    RejectionReason.INTERNAL_RECIPIENT_ERROR,
//                    "Response could not be constructed.",
//                    connector.getId(), connector.getOutboundModelVersion());
//        } catch (RuntimeException exception) {
//            LOGGER.warn("Could not process contract request. [id=({}), exception=({})]",
//                    requestMessage.getId(), exception.getMessage());
//            return ErrorResponse.withDefaultHeader(
//                    RejectionReason.BAD_PARAMETERS,
//                    "Malformed contract request.",
//                    connector.getId(), connector.getOutboundModelVersion());
//        }
//    }
//
//    /**
//     * Accept contract by building a {@link ContractAgreement} and sending it as payload with a
//     * {@link ContractAgreementMessage}.
//     *
//     * @param contractRequest The contract request object from the data consumer.
//     * @return The message response to the requesting connector.
//     */
//    private MessageResponse acceptContract(ContractRequest contractRequest)
//            throws UUIDFormatException, MessageException {
//
//        final var header = messageService.buildContractAgreementMessage(requestMessage.getIssuerConnector(), requestMessage.getId());
//        // Turn the accepted contract request into a contract agreement.
//        final var contractAgreement = pmp.buildAgreementFromContract(contractRequest);
//
//        // Send response to the data consumer.
//        return BodyResponse.create(header, contractAgreement.toRdf());
//    }
//
//    /**
//     * Builds a contract rejection message with a rejection reason.
//     *
//     * @param recipient The issuer connector.
//     * @param correlationMessage The correlation message id.
//     * @return A contract rejection message.
//     */
//    private MessageResponse rejectContract(final URI recipient, final URI correlationMessage) {
//        final var desc = new ContractRejectionMessageDesc(correlationMessage);
//        final var header = (RejectionMessage) rejectionService.buildMessage(recipient, desc);
//        return ErrorResponse.create(header, "Contract rejected.");
//    }
}
