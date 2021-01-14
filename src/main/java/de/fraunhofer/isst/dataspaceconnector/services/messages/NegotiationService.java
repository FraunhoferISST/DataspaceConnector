package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RequestFormatException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.UnsupportedPatternException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.ResponseService.ResponseType;
import de.fraunhofer.isst.dataspaceconnector.services.messages.request.ContractRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.response.ContractResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

@Service
public class NegotiationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NegotiationService.class);

    private final PolicyHandler policyHandler;
    private final ContractRequestService contractRequestService;
    private final ContractResponseService contractResponseService;
    private final SerializerProvider serializerProvider;
    private final ConfigurationContainer configurationContainer;

    private boolean status;
    private URI recipient;

    @Autowired
    public NegotiationService(ContractRequestService contractRequestService,
        ContractResponseService contractResponseService,
        PolicyHandler policyHandler, SerializerProvider serializerProvider,
        ConfigurationContainer configurationContainer)
        throws IllegalArgumentException {
        if (contractRequestService == null)
            throw new IllegalArgumentException("The ContractRequestService cannot be null.");

        if (contractResponseService == null)
            throw new IllegalArgumentException("The ContractResponseService cannot be null.");

        if (policyHandler == null)
            throw new IllegalArgumentException("The PolicyHandler cannot be null.");

        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        this.contractRequestService = contractRequestService;
        this.contractResponseService = contractResponseService;
        this.policyHandler = policyHandler;
        this.status = true;
        this.serializerProvider = serializerProvider;
        this.configurationContainer = configurationContainer;
    }

    /**
     * Deserialize contract and send it as contract request message.
     *
     * @return The http response.
     * @throws IllegalArgumentException - if the contract could not be deserialized.
     * @throws MessageException - if the contract request message could not be sent.
     */
    public Response sendContractRequest(String contractAsString, URI artifactId, URI recipient)
        throws IllegalArgumentException, MessageException {
        this.recipient = recipient;

        Contract contract;
        try {
            // Validate contract input.
            contract = policyHandler.validateContract(contractAsString);
        } catch (RequestFormatException exception) {
            LOGGER.warn("Could not deserialize contract. [exception=({})]",
                exception.getMessage());
            throw new RequestFormatException("Malformed contract. " + exception.getMessage());
        }

        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        // Build contract request. TODO: Change to curator or maintainer?
        ContractRequest request = fillContract(artifactId, connector.getId(),
            contractRequestService.buildContractRequest(contract));

        try {
            // Send ContractRequestMessage.
            contractRequestService.setParameter(recipient, request.getId());
            return contractRequestService.sendMessage(request.toRdf());
        } catch (MessageException exception) {
            // Failed to send a contract request message.
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
    public URI contractAccepted(Map<ResponseType, String> map, String header) throws ContractException,
        MessageException {
        final var payload = map.get(ResponseType.CONTRACT_AGREEMENT);
        if (payload != null) {
            Contract contract;
            try {
                // Validate received contract.
                contract = policyHandler.validateContract(payload);
            } catch (UnsupportedPatternException exception) {
                LOGGER.warn("Could not deserialize contract. [exception=({})]",
                    exception.getMessage());
                throw new UnsupportedPatternException("Malformed contract. " + exception.getMessage());
            }

            /*Response response; TODO: Error "Incoming Messages must be subtype of RequestMessage
                                  or NotificationMessage!" (Framework Issue)
                                  TODO: Update resource contract for enforcement. (later)
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
                contractResponseService.setParameter(recipient, correlationMessage, contract.getId());
                ContractAgreement agreement = contractResponseService.buildContractAgreement(contract);
                response = contractResponseService.sendMessage(agreement.toRdf());
            } catch (MessageException exception) {
                // Failed to send a contract agreement message
                LOGGER.warn("Could not connect to request message service. [exception=({})]",
                    exception.getMessage());
                throw new MessageNotSentException("Could not send contract agreement message. "
                    + exception.getMessage());
            }
            if (response != null) {
                LOGGER.warn("Received unexpected response" + response.body().toString());
            } else {
                return null;
            }*/

            return contract.getId();
        } else {
            return null;
        }
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Add artifact id to every rule in this contract.
     *
     * @return A valid contract request.
     */
    private ContractRequest fillContract(URI artifactId, URI consumer,
        ContractRequest contractRequest) {
        ContractRequestImpl request = (ContractRequestImpl) contractRequest;

        final var obligations = request.getObligation();
        final var permissions = request.getPermission();
        final var prohibitions = request.getProhibition();

        if (obligations != null && !obligations.isEmpty()) {
            for (Rule r : obligations)
                ((DutyImpl) r).setTarget(artifactId);
        }

        if (permissions != null && !permissions.isEmpty()) {
            for (Rule r : permissions)
                ((PermissionImpl) r).setTarget(artifactId);
        }

        if (prohibitions != null && !prohibitions.isEmpty()) {
            for (Rule r : prohibitions)
                ((ProhibitionImpl) r).setTarget(artifactId);
        }

        // Add consumer to contract.
        request.setConsumer(consumer);
        return request;
    }

    /**
     * Compare the contracts to each other.
     *
     * @return True is the content is equal, false if any difference is detected.
     */
    public boolean compareContracts(Contract request, Contract offer) {
        if (request == null || offer == null)
            return false;

        boolean permissions;
        if (request.getPermission() != null && offer.getPermission() != null) {
            permissions = comparePermissions(request.getPermission(), offer.getPermission());
        } else {
            permissions = true;
        }

        boolean prohibitions;
        if (request.getProhibition() != null && offer.getProhibition() != null) {
            prohibitions = compareRules(request.getProhibition(), offer.getProhibition());
        } else {
            prohibitions = true;
        }

        boolean obligations;
        if (request.getObligation() != null && offer.getObligation() != null) {
            obligations = compareRules(request.getObligation(), offer.getObligation());
        } else {
            obligations = true;
        }

        return permissions && prohibitions && obligations;
    }

    /**
     * Compares the content of two permissions.
     *
     * @return True is the content is equal, false if any difference is detected.
     */
    private boolean comparePermissions(
        ArrayList<? extends Permission> request, ArrayList<? extends Permission> offer) {
        if (request.size() != offer.size())
            return false;

        for (int i = 0; i < request.size(); i++) {
            if (request.get(i).getPostDuty() != null && offer.get(i).getPostDuty() != null) {
                for (int j = 0; i < request.get(i).getPostDuty().size(); i++) {
                    if (!request.get(i).getPostDuty().get(j).toRdf()
                        .equals(offer.get(i).getPostDuty().get(j).toRdf()))
                        return false;
                }
            }

            if (request.get(i).getPreDuty() != null && offer.get(i).getPreDuty() != null) {
                for (int j = 0; i < request.get(i).getPreDuty().size(); i++) {
                    if (!request.get(i).getPreDuty().get(j).toRdf()
                        .equals(offer.get(i).getPreDuty().get(j).toRdf()))
                        return false;
                }
            }

            if (request.get(i).getConstraint() != null && offer.get(i).getConstraint() != null) {
                for (int j = 0; i < request.get(i).getConstraint().size(); i++) {
                    if (!request.get(i).getConstraint().get(j).toRdf()
                        .equals(offer.get(i).getConstraint().get(j).toRdf()))
                        return false;
                }
            }

            if (!request.get(i).getAction().get(0).toRdf()
                .equals(offer.get(i).getAction().get(0).toRdf()))
                return false;

            i++;
        }

        return true;
    }

    /**
     * Compares the content of two prohibitions or obligations.
     *
     * @return True is the content is equal, false if any difference is detected.
     */
    private boolean compareRules(
        ArrayList<? extends Rule> request, ArrayList<? extends Rule> offer) {
        if (request.size() != offer.size())
            return false;

        for (int i = 0; i < request.size(); i++) {
            if (request.get(i).getConstraint() != null && offer.get(i).getConstraint() != null) {
                for (int j = 0; i < request.get(i).getConstraint().size(); i++) {
                    if (!request.get(i).getConstraint().get(j).toRdf()
                        .equals(offer.get(i).getConstraint().get(j).toRdf()))
                        return false;
                }
            }

            if (!request.get(i).getAction().get(0).toRdf()
                .equals(offer.get(i).getAction().get(0).toRdf()))
                return false;

            i++;
        }

        return true;
    }
}
