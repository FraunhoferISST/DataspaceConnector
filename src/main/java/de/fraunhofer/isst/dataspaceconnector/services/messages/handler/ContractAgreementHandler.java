package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.ContractAgreementMessageImpl;

/**
 * This @{@link ContractAgreementHandler} handles all incoming messages that have a
 * {@link ContractAgreementMessageImpl} as part one in the multipart message.
 * This header must have the correct '@type' reference as defined in the
 * {@link ContractAgreementMessageImpl} JsonTypeName annotation.
 */
//@Component
//@SupportedMessageType(ContractAgreementMessageImpl.class)
//@RequiredArgsConstructor
//public class ContractAgreementHandler implements MessageHandler<ContractAgreementMessageImpl> {

//    public static final Logger LOGGER = LoggerFactory.getLogger(ContractAgreementHandler.class);
//
//    /**
//     * The clearing house access url.
//     */
//    @Value("${clearing.house.url}")
//    private String clearingHouse;
//
//    private final @NonNull ConfigurationContainer configurationContainer;
//    private final @NonNull PolicyManagementService pmp;
//    private final @NonNull NotificationService messageService;
//    private final @NonNull ContractAgreementService contractAgreementService;
//
//    /**
//     * This message implements the logic that is needed to handle the message. As it just returns
//     * the input as string the messagePayload-InputStream is converted to a String.
//     *
//     * @param message The received contract agreement message.
//     * @param messagePayload The message's content.
//     * @return The response message.
//     * @throws RuntimeException if the response body failed to be build.
//     */
//    @Override
//    public MessageResponse handleMessage(ContractAgreementMessageImpl message,
//                                         MessagePayload messagePayload) throws RuntimeException {
//        MessageUtils.checkForEmptyMessage(message);
//        exceptionService.checkForVersionSupport(message.getModelVersion());
//
//        // Get a local copy of the current connector.
//        var connector = configurationContainer.getConnector();
//
//        // Read message payload as string.
//        String payload;
//        try {
//            payload = IOUtils
//                    .toString(messagePayload.getUnderlyingInputStream(), StandardCharsets.UTF_8);
//            // If request is empty, return rejection message.
//            if (payload.equals("")) {
//                LOGGER.debug("Contract agreement is missing [id=({}), payload=({})]",
//                        message.getId(), payload);
//                return ErrorResponse
//                        .withDefaultHeader(RejectionReason.BAD_PARAMETERS,
//                                "Missing contract agreement.",
//                                connector.getId(), connector.getOutboundModelVersion());
//            }
//        } catch (IOException e) {
//            LOGGER.debug("Cannot read payload. [id=({}), payload=({})]",
//                    message.getId(), messagePayload);
//            return ErrorResponse
//                    .withDefaultHeader(RejectionReason.BAD_PARAMETERS,
//                            "Malformed payload.",
//                            connector.getId(), connector.getOutboundModelVersion());
//        }
//
//        try {
//            saveContract(payload);
//
//            // Build response header.
//            final var header = messageService.buildMessageProcessedNotification(message.getIssuerConnector(), message.getId());
//            return BodyResponse.create(header, "Message processed. The contract is legal.");
//        } catch (ContractException exception) {
//            LOGGER.warn("Failed to store the contract agreement. [exception=({})]",
//                    exception.getMessage());
//            return ErrorResponse.withDefaultHeader(
//                    RejectionReason.INTERNAL_RECIPIENT_ERROR,
//                    "Failed to store the contract agreement. Thus, it is not legal. " +
//                            "Please try again.",
//                    connector.getId(), connector.getOutboundModelVersion());
//        }
//    }
//
//
//    /**
//     * Saves the contract agreement to the internal database and sendMessage it to the ids clearing house.
//     *
//     * @param agreement The contract agreement from the data consumer.
//     */
//    private void saveContract(String agreement) throws ContractException, MessageException {
//
//        ContractAgreement contractAgreement = pmp.deserializeContractAgreement(agreement);
//
//        try {
//            // Save contract agreement to database.
//            UUID uuid = UUIDUtils.uuidFromUri(contractAgreement.getId());
//            contractAgreementService.addContract(new ResourceContract(uuid, contractAgreement.toRdf()));
//        } catch (Exception exception) {
//            LOGGER.warn("Failed to store the contract agreement. [exception=({})]",
//                    exception.getMessage());
//            throw new ContractException("Could not save contract agreement.");
//        }
//
//        // Send ContractAgreement to the ClearingHouse.
//        // TODO: Activate Clearing House communication as soon as it accepts IM 4.
//        try {
//            messageService.sendLogMessage(URI.create(clearingHouse), contractAgreement.toRdf());
//        } catch (MessageBuilderException exception) {
//            // Failed to build the log message.
//            LOGGER.warn("Failed to build log message. [exception=({})]", exception.getMessage());
//        } catch (MessageResponseException exception) {
//            // Failed to read the response message.
//            LOGGER.debug("Received invalid ids response. [exception=({})]", exception.getMessage());
//        } catch (MessageNotSentException exception) {
//            // Failed to sendMessage the log message.
//            LOGGER.warn("Failed to sendMessage a log message. [exception=({})]", exception.getMessage());
//        }
//    }
//}
