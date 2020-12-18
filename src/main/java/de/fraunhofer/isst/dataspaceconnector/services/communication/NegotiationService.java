package de.fraunhofer.isst.dataspaceconnector.services.communication;

import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnsupportedPatternException;
import de.fraunhofer.isst.dataspaceconnector.services.communication.MessageResponseService.ResponseType;
import de.fraunhofer.isst.dataspaceconnector.services.communication.request.ContractRequestMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.communication.response.ContractResponseMessageService;
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

    public static final Logger LOGGER = LoggerFactory.getLogger(NegotiationService.class);

    private PolicyHandler policyHandler;
    private ContractRequestMessageService contractRequestMessageService;
    private ContractResponseMessageService contractResponseMessageService;
    private boolean status;

    @Autowired
    public NegotiationService(ContractRequestMessageService contractRequestMessageService,
        ContractResponseMessageService contractResponseMessageService,
        PolicyHandler policyHandler) throws IllegalArgumentException {
        if (contractRequestMessageService == null)
            throw new IllegalArgumentException("The ContractRequestMessageService cannot be null.");

        if (contractResponseMessageService == null)
            throw new IllegalArgumentException("The ContractResponseMessageService cannot be null.");

        if (policyHandler == null)
            throw new IllegalArgumentException("The PolicyHandler cannot be null.");

        this.contractRequestMessageService = contractRequestMessageService;
        this.contractResponseMessageService = contractResponseMessageService;
        this.policyHandler = policyHandler;
        this.status = true;
    }

    public Response startSequence(String contractAsString, URI recipient)
        throws ContractException, MessageException {
        Contract contract;
        try {
            contract = policyHandler.validateContract(contractAsString);
        } catch (UnsupportedPatternException exception) {
            LOGGER.warn("Could not deserialize contract. [exception=({})]",
                exception.getMessage());
            throw new UnsupportedPatternException("Malformed contract. " + exception.getMessage());
        }

        ContractRequest contractRequest = contractRequestMessageService.buildContractRequest(contract);

        try {
            // Send ContractRequestMessage.
            contractRequestMessageService.setParameter(recipient, contractRequest.getId());
            return contractRequestMessageService.sendMessage(contractRequestMessageService,
                contractRequest.toRdf());
        } catch (MessageException exception) {
            // Failed to send a description request message
            LOGGER.warn("Could not connect to request message service. [exception=({})]",
                exception.getMessage());
            throw new MessageNotSentException("Error in message service. " + exception.getMessage());
        }
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
