package de.fraunhofer.isst.dataspaceconnector.services.ids;

import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.InfrastructureComponent;
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
    public ConfigurationModel deserializeConfigurationModel(final String config)
            throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(config, ConfigurationModel.class);
        } catch (IOException exception) {
            LOGGER.warn("Could not deserialize config model. [exception=({})]", exception.getMessage());
            throw new IllegalArgumentException("Could not deserialize input.", exception);
        }
    }

    /**
     * Deserialize string to ids infrastructure component.
     *
     * @param component The infrastructure component string.
     * @return The ids object.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public InfrastructureComponent deserializeInfrastructureComponent(final String component)
            throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(component, InfrastructureComponent.class);
        } catch (IOException exception) {
            LOGGER.debug("Could not deserialize component. [exception=({})]", exception.getMessage());
            throw new IllegalArgumentException("Could not deserialize input.", exception);
        }
    }

    /**
     * Deserialize string to ids resource.
     *
     * @param resource The resource string.
     * @return The ids object.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public Resource deserializeResource(final String resource) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(resource, Resource.class);
        } catch (IOException exception) {
            LOGGER.debug("Could not deserialize resource. [exception=({})]", exception.getMessage());
            throw new IllegalArgumentException("Could not deserialize input.", exception);
        }
    }

    /**
     * Returns the ids header of a http multipart response.
     *
     * @param response A ids response message.
     * @return The response message.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public ResponseMessage deserializeResponseMessage(final String response) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(response, ResponseMessage.class);
        } catch (IOException exception) {
            LOGGER.warn("Could not deserialize response message. [exception=({})]", exception.getMessage());
            throw new IllegalArgumentException("Could not deserialize response message.", exception);
        }
    }

    /**
     * Deserialize string to ids rule.
     *
     * @param policy The policy string.
     * @return The ids rule.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public Rule deserializeRule(final String policy) throws IllegalArgumentException {
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
    public Contract deserializeContract(final String contract) throws IllegalArgumentException {
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
    public ContractAgreement deserializeContractAgreement(final String contract) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(contract,
                    ContractAgreement.class);
        } catch (IOException exception) {
            LOGGER.warn("Could not deserialize agreement. [exception=({})]",
                    exception.getMessage());
            throw new IllegalArgumentException("Could not deserialize contract agreement.", exception);
        }
    }

    /**
     * Deserialize string to ids contract request.
     *
     * @param contract The contract string.
     * @return The ids contract request.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public ContractRequest deserializeContractRequest(final String contract) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(contract, ContractRequest.class);
        } catch (IOException exception) {
            LOGGER.warn("Could not deserialize request. [exception=({})]", exception.getMessage());
            throw new IllegalArgumentException("Could not deserialize contract request.", exception);
        }
    }

    /**
     * Deserialize string to ids contract offer.
     *
     * @param contract The contract string.
     * @return The ids contract offer.
     * @throws IllegalArgumentException If deserialization fails.
     */
    public ContractOffer deserializeContractOffer(final String contract) throws IllegalArgumentException {
        try {
            return serializerProvider.getSerializer().deserialize(contract, ContractOffer.class);
        } catch (IOException exception) {
            LOGGER.warn("Could not deserialize offer. [exception=({})]", exception.getMessage());
            throw new IllegalArgumentException("Could not deserialize contract offer.", exception);
        }
    }
}
