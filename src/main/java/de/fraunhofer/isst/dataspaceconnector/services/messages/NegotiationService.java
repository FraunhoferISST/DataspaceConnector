package de.fraunhofer.isst.dataspaceconnector.services.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestImpl;
import de.fraunhofer.iais.eis.DutyImpl;
import de.fraunhofer.iais.eis.PermissionImpl;
import de.fraunhofer.iais.eis.ProhibitionImpl;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.UnsupportedPatternException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageResponseService.ResponseType;
import de.fraunhofer.isst.dataspaceconnector.services.messages.request.ContractAgreementMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.request.ContractRequestMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NegotiationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NegotiationService.class);

    private final PolicyHandler policyHandler;
    private final ContractRequestMessageService contractRequestMessageService;
    private final ContractAgreementMessageService contractAgreementMessageService;
    private final SerializerProvider serializerProvider;
    private boolean status;
    private URI recipient;

    @Autowired
    public NegotiationService(ContractRequestMessageService contractRequestMessageService,
        ContractAgreementMessageService contractAgreementMessageService,
        PolicyHandler policyHandler, SerializerProvider serializerProvider)
        throws IllegalArgumentException {
        if (contractRequestMessageService == null)
            throw new IllegalArgumentException("The ContractRequestMessageService cannot be null.");

        if (contractAgreementMessageService == null)
            throw new IllegalArgumentException("The ContractAgreementMessageService cannot be null.");

        if (policyHandler == null)
            throw new IllegalArgumentException("The PolicyHandler cannot be null.");

        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        this.contractRequestMessageService = contractRequestMessageService;
        this.contractAgreementMessageService = contractAgreementMessageService;
        this.policyHandler = policyHandler;
        this.status = true;
        this.serializerProvider = serializerProvider;
    }

    /**
     * Deserialize contract and send it as contract request message.
     *
     * @return The http response.
     * @throws ContractException - if the contract could not be read.
     * @throws MessageException - if the contract request message could not be sent.
     */
    public Response startSequence(String contractAsString, URI artifactId, URI recipient)
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

        ContractRequest request = fillContract(artifactId,
            contractRequestMessageService.buildContractRequest(contract));

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

    /**
    * Check if the contract request has been successful. If yes, send a contract agreement message.
    *
    * @return The contract agreement id.
    * @throws ContractException - if the contract could not be read.
    * @throws MessageException - if the contract request message could not be sent.
    */
    public URI contractAccepted(Map<ResponseType, String> map)
        throws ContractException, MessageException {
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

            Response response;
            try {
                // Send ContractAgreementMessage to recipient.
                contractAgreementMessageService.setParameter(recipient);
                response = contractAgreementMessageService
                    .sendMessage(contractAgreementMessageService, agreement.toRdf());
            } catch (MessageException exception) {
                // Failed to send a contract agreement message
                LOGGER.warn("Could not connect to request message service. [exception=({})]",
                    exception.getMessage());
                throw new MessageNotSentException("Could not send contract agreement message. "
                    + exception.getMessage());
            }

            return contract.getId();
        } else {
            return null;
        }
    }

    /**
     * Compare the content of to rule lists to each other.
     *
     * @param request List of rules of the contract request.
     * @param offer List of rules of the contract offer.
     * @return True is the content is equal, false if any difference is detected.
     */
    public boolean compareRule(ArrayList<? extends Rule> request, ArrayList<? extends Rule> offer) {
        if (request == null && offer == null) {
            return true;
        } else if (request == null) {
            return false;
        } else if (offer == null) {
            return false;
        }

        if (request.size() != offer.size()) {
            return false;
        }

        for (int i = 0; i < request.size(); i++) {
            Rule requestRule = request.get(i);
            Rule offerRule = offer.get(i);
            try {
                String requestString = serializerProvider.getSerializer().serializePlainJson(requestRule);
                String offerString = serializerProvider.getSerializer().serializePlainJson(offerRule);
                if (!requestString.equals(offerString)) {
                    return false;
                }
            } catch (JsonProcessingException e) {
                return false;
            }
            i++;
        }
        return true;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    private ContractRequest fillContract(URI artifactId, ContractRequest contractRequest) {
        ContractRequestImpl request = (ContractRequestImpl) contractRequest;

        final var obligations = request.getObligation();
        final var permissions = request.getPermission();
        final var prohibitions = request.getProhibition();

        if (obligations != null && !obligations.isEmpty()) {
            for (Rule r : obligations) {
                ((DutyImpl) r).setTarget(artifactId);
            }
        }

        if (permissions != null && !permissions.isEmpty()) {
            for (Rule r : permissions) {
                ((PermissionImpl) r).setTarget(artifactId);
            }
        }

        if (prohibitions != null && !prohibitions.isEmpty()) {
            for (Rule r : prohibitions) {
                ((ProhibitionImpl) r).setTarget(artifactId);
            }
        }

        return request;
    }
}
