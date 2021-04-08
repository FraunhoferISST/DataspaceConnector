package de.fraunhofer.isst.dataspaceconnector.services.ids;

import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.InfrastructureComponent;
import de.fraunhofer.iais.eis.NotificationMessage;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResponseMessage;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service class for ids object deserialization.
 */
@Service
@RequiredArgsConstructor
public class DeserializationService {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeserializationService.class);

    /**
     * Service for ids serializations.
     */
    private final @NonNull SerializerProvider serializerProvider;

    /**
     * Deserialize string to ids configuration model.
     *
     * @param config The configuration model string.
     * @return The ids configuration model.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public ConfigurationModel getConfigurationModel(final String config) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(config, ConfigurationModel.class);
        } catch (IOException e) {
            LOGGER.warn("Could not deserialize config model. [exception=({})]", e.getMessage());
            throw new IllegalArgumentException("Could not deserialize input.", e);
        }
    }

    /**
     * Deserialize string to ids infrastructure component.
     *
     * @param component The infrastructure component string.
     * @return The ids object.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public InfrastructureComponent getInfrastructureComponent(final String component) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(component,
                    InfrastructureComponent.class);
        } catch (IOException e) {
            LOGGER.debug("Could not deserialize component. [exception=({})]", e.getMessage());
            throw new IllegalArgumentException("Could not deserialize input.", e);
        }
    }

    /**
     * Deserialize string to ids resource.
     *
     * @param resource The resource string.
     * @return The ids object.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public Resource getResource(final String resource) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(resource, Resource.class);
        } catch (IOException e) {
            LOGGER.debug("Could not deserialize resource. [exception=({})]", e.getMessage());
            throw new IllegalArgumentException("Could not deserialize input.", e);
        }
    }

    /**
     * Returns the ids header of an http multipart response if the header is of type
     * ResponseMessage.
     *
     * @param response A ids response message.
     * @return The response message.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public ResponseMessage getResponseMessage(final String response) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(response, ResponseMessage.class);
        } catch (IOException e) {
            LOGGER.warn("Could not deserialize response message. [exception=({})]", e.getMessage());
            throw new IllegalArgumentException("Could not deserialize response message.", e);
        }
    }

    /**
     * Returns the ids header of an http multipart response if the header is of type
     * NotificationMessage.
     *
     * @param response A ids response message.
     * @return The notification message.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public NotificationMessage getNotificationMessage(final String response) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(response, NotificationMessage.class);
        } catch (IOException e) {
            LOGGER.warn("Could not deserialize response message. [exception=({})]", e.getMessage());
            throw new IllegalArgumentException("Could not deserialize response message.", e);
        }
    }

    /**
     * Deserialize string to ids rule.
     *
     * @param policy The policy string.
     * @return The ids rule.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public Rule getRule(final String policy) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(policy, Rule.class);
        } catch (IOException exception) {
            LOGGER.warn("Could not deserialize rule. [exception=({})]", exception.getMessage());
            throw new IllegalArgumentException("Could not deserialize rule.", exception);
        }
    }

    /**
     * Deserialize string to ids contract.
     *
     * @param contract The contract string.
     * @return The ids contract.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public Contract getContract(final String contract) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(contract, Contract.class);
        } catch (IOException exception) {
            LOGGER.warn("Could not deserialize contract. [exception=({})]", exception.getMessage());
            throw new IllegalArgumentException("Could not deserialize contract.", exception);
        }
    }

    /**
     * Deserialize string to ids contract agreement.
     *
     * @param contract The contract string.
     * @return The ids contract agreement.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public ContractAgreement getContractAgreement(final String contract) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(contract,
                    ContractAgreement.class);
        } catch (IOException e) {
            LOGGER.warn("Could not deserialize agreement. [exception=({})]", e.getMessage());
            throw new IllegalArgumentException("Could not deserialize contract agreement.", e);
        }
    }

    /**
     * Deserialize string to ids contract request.
     *
     * @param contract The contract string.
     * @return The ids contract request.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public ContractRequest getContractRequest(final String contract) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(contract, ContractRequest.class);
        } catch (IOException e) {
            LOGGER.warn("Could not deserialize request. [exception=({})]", e.getMessage());
            throw new IllegalArgumentException("Could not deserialize contract request.", e);
        }
    }

    /**
     * Deserialize string to ids contract offer.
     *
     * @param contract The contract string.
     * @return The ids contract offer.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public ContractOffer getContractOffer(final String contract) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(contract, ContractOffer.class);
        } catch (IOException exception) {
            LOGGER.warn("Could not deserialize offer. [exception=({})]", exception.getMessage());
            throw new IllegalArgumentException("Could not deserialize contract offer.", exception);
        }
    }
}
