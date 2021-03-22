package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Contains methods required for policy negotiation. TODO
 */
@Service
@RequiredArgsConstructor
public class NegotiationService {

    /*private static final Logger LOGGER = LoggerFactory.getLogger(NegotiationService.class);

    private final PolicyDecisionService policyDecisionService;
    private final ResponseMessageService responseMessageService;
    private final SerializerProvider serializerProvider;
    private final ConfigurationContainer configurationContainer;
    private final @NonNull PolicyManagementService pmp;


    *//**
     * Checks if the contract request has been successful and, if so, sends a contract agreement message.
     *
     * @param recipient recipient of the contract agreement message.
     * @param header message header.
     * @param payload message payload.
     * @return the contract agreement ID.
     * @throws ContractException if the contract could not be read.
     * @throws MessageException if the contract request message could not be sent.
     *//*
    public URI contractAccepted(URI recipient, String header, String payload) throws ContractException,
        MessageException {
        if (payload != null && !payload.equals("")) {
            Contract contract;
            try {
                // Validate received contract.
                contract = pmp.deserializeContract(payload);
            } catch (UnsupportedPatternException exception) {
                LOGGER.warn("Could not deserialize contract. [exception=({})]",
                    exception.getMessage());
                throw new UnsupportedPatternException("Malformed contract. " + exception.getMessage());
            }

            Map<String, String> response;
            try {
                // Get correlation message.
                URI correlationMessage;
                try {
                    ContractAgreementMessage message = serializerProvider.getSerializer()
                        .deserialize(header, ContractAgreementMessage.class);
                    correlationMessage = message.getCorrelationMessage();
                } catch (IOException exception) {
                    throw new MessageResponseException("Could not read contract agreement.");
                }

                // Send ContractAgreementMessage to recipient.
                ContractAgreement agreement = pmp.buildAgreementFromContract(contract, contract.getId());
                response = responseMessageService.sendContractAgreement(recipient, correlationMessage, agreement.toRdf());
            } catch (MessageBuilderException exception) {
                // Failed to build the contract agreement message.
                LOGGER.warn("Failed to build a request. [exception=({})]", exception.getMessage());
                throw new MessageNotSentException("Failed to build the ids message. " +
                        "[exception=({})]", exception);
            } catch (MessageResponseException exception) {
                // Failed to read the contract agreement message.
                LOGGER.debug("Received invalid ids response. [exception=({})]", exception.getMessage());
                throw new MessageResponseException("Failed to read the ids response message. " +
                        "[exception=({})]", exception);
            } catch (MessageNotSentException exception) {
                // Failed to sendMessage the contract agreement message.
                LOGGER.warn("Failed to sendMessage a request. [exception=({})]", exception.getMessage());
                throw new MessageNotSentException("Failed to sendMessage the ids message. " +
                        "[exception=({})]", exception);
            }

            if (response != null) {
                return contract.getId();
            } else {
                // Failed to read the contract response message.
                LOGGER.info("Received invalid ids response.");
                throw new MessageResponseException("Failed to read the ids response message.");
            }
        } else {
            // Failed to read the contract response message.
            LOGGER.info("Received no valid contract.");
            throw new MessageResponseException("Failed to read the ids response message.");
        }
    }**/
}
