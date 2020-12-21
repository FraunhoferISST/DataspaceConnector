package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.UnsupportedPatternException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageResponseService.ResponseType;
import de.fraunhofer.isst.dataspaceconnector.services.messages.request.ContractAgreementMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.request.ContractRequestMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import java.net.URI;
import java.util.Map;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NegotiationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NegotiationService.class);
    private static final String CLEARING_HOUSE = "https://ch-ids.aisec.fraunhofer.de/logs/messages/";

    private PolicyHandler policyHandler;
    private ContractRequestMessageService contractRequestMessageService;
    private ContractAgreementMessageService contractAgreementMessageService;
    private boolean status;
    private URI recipient;
    private URI agreementId;

    @Autowired
    public NegotiationService(ContractRequestMessageService contractRequestMessageService,
        ContractAgreementMessageService contractAgreementMessageService,
        PolicyHandler policyHandler) throws IllegalArgumentException {
        if (contractRequestMessageService == null)
            throw new IllegalArgumentException("The ContractRequestMessageService cannot be null.");

        if (contractAgreementMessageService == null)
            throw new IllegalArgumentException("The ContractAgreementMessageService cannot be null.");

        if (policyHandler == null)
            throw new IllegalArgumentException("The PolicyHandler cannot be null.");

        this.contractRequestMessageService = contractRequestMessageService;
        this.contractAgreementMessageService = contractAgreementMessageService;
        this.policyHandler = policyHandler;
        this.status = true;
    }

    /**
     * Deserialize contract and send it as contract request message.
     *
     * @return The http response.
     * @throws ContractException - if the contract could not be read.
     * @throws MessageException - if the contract request message could not be sent.
     */
    public Response startSequence(String contractAsString, URI recipient)
        throws ContractException, MessageException {
        this.recipient = recipient;

        Contract contract;
        try {
            contract = policyHandler.validateContract(contractAsString);
        } catch (UnsupportedPatternException exception) {
            LOGGER.warn("Could not deserialize contract. [exception=({})]",
                exception.getMessage());
            throw new UnsupportedPatternException("Malformed contract. " + exception.getMessage());
        }

        ContractRequest request = contractRequestMessageService.buildContractRequest(contract);

        try {
            // Send ContractRequestMessage.
            contractRequestMessageService.setParameter(recipient, request.getId());
            return contractRequestMessageService.sendMessage(contractRequestMessageService,
                request.toRdf());
        } catch (MessageException exception) {
            // Failed to send a contract request message
            LOGGER.warn("Could not connect to request message service. [exception=({})]",
                exception.getMessage());
            throw new MessageNotSentException("Error in message service. " + exception.getMessage());
        }
    }

    public boolean contractAccepted(Map<ResponseType, String> map) throws ContractException,
        MessageException {
        final var payload = map.get(ResponseType.CONTRACT_AGREEMENT);
        if (payload != null) {
            Contract contract;
            try {
                contract = policyHandler.validateContract(payload);
            } catch (UnsupportedPatternException exception) {
                LOGGER.warn("Could not deserialize contract. [exception=({})]",
                    exception.getMessage());
                throw new UnsupportedPatternException("Malformed contract. " + exception.getMessage());
            }

            ContractAgreement agreement = contractAgreementMessageService.buildContractAgreement(contract);
            this.agreementId = agreement.getId();

            Response response;
            try {
                // Send ContractAgreementMessage to recipient.
                contractAgreementMessageService.setParameter(recipient, agreement.getId());
                response = contractAgreementMessageService
                    .sendMessage(contractAgreementMessageService, agreement.toRdf());
            } catch (MessageException exception) {
                // Failed to send a contract agreement message
                LOGGER.warn("Could not connect to request message service. [exception=({})]",
                    exception.getMessage());
                throw new MessageNotSentException("Error in message service. " + exception.getMessage());
            }

            LOGGER.info("Response: " + response);

            return true;
        } else {
            return false;
        }
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public URI getAgreementId() {
        return agreementId;
    }
}
