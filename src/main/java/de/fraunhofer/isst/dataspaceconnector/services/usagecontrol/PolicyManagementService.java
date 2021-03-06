package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Prohibition;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidContractException;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

@Service
@RequiredArgsConstructor
public class PolicyManagementService {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyManagementService.class);

    /**
     * Service for ids serializations.
     */
    private final @NonNull SerializerProvider serializerProvider;

    /**
     * The current connector configuration.
     */
    private final @NonNull ConfigurationContainer configurationContainer;

    /**
     * Get stored contract agreement for requested element.
     *
     * @param element The requested element.
     * @return The respective contract agreement.
     */
    public ContractAgreement getContractAgreementForRequestedElement(final URI element) {
        // TODO Get abstract entity by id (resource, artifact, catalog)
        return null;
    }

    /**
     * Iterate over all rules of a contract agreement and add the ones with the element as their
     * target to a rule list.
     *
     * @param agreement The contract agreement.
     * @param element   The requested element.
     * @return List of ids rules.
     */
    public List<Rule> getRulesForRequestedElement(final ContractAgreement agreement,
                                                  final URI element) {
        List<Rule> rules = new ArrayList<>();

        for (Permission permission : agreement.getPermission()) {
            final var target = permission.getTarget();
            if (element == target) {
                rules.add(permission);
            }
        }

        for (Prohibition prohibition : agreement.getProhibition()) {
            final var target = prohibition.getTarget();
            if (element == target) {
                rules.add(prohibition);
            }
        }

        for (Duty obligation : agreement.getObligation()) {
            final var target = obligation.getTarget();
            if (element == target) {
                rules.add(obligation);
            }
        }

        return rules;
    }

    /**
     * Build the contract request.
     *
     * @param contract The contract.
     * @return The contract request.
     */
    public ContractRequest buildRequestFromContract(final Contract contract) throws ContractBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new ContractRequestBuilder()
                ._consumer_(connector.getMaintainer())
                ._provider_(contract.getProvider())
                ._contractDate_(getGregorianNow())
                ._contractStart_(getGregorianNow())
                ._obligation_(contract.getObligation())
                ._permission_(contract.getPermission())
                ._prohibition_(contract.getProhibition())
                ._provider_(contract.getProvider())
                .build();
    }

    /**
     * Build contract agreement.
     *
     * @param contract The contract.
     * @return The contract agreement.
     */
    public ContractAgreement buildAgreementFromContract(final Contract contract) throws ContractBuilderException {
        return new ContractAgreementBuilder()
                ._consumer_(contract.getConsumer())
                ._provider_(contract.getProvider())
                ._contractDate_(contract.getContractDate())
                ._contractStart_(contract.getContractStart())
                ._obligation_(contract.getObligation())
                ._permission_(contract.getPermission())
                ._prohibition_(contract.getProhibition())
                ._provider_(contract.getProvider())
                .build();
    }

    /**
     * Build contract agreement. Keeps parameters and id.
     *
     * @param contract   The contract.
     * @param contractId The id of the contract.
     * @return The contract agreement.
     */
    public ContractAgreement buildAgreementFromContract(final Contract contract,
                                                        final URI contractId) throws ContractBuilderException {
        return new ContractAgreementBuilder(contractId)
                ._consumer_(contract.getConsumer())
                ._provider_(contract.getProvider())
                ._contractDate_(contract.getContractDate())
                ._contractStart_(contract.getContractStart())
                ._obligation_(contract.getObligation())
                ._permission_(contract.getPermission())
                ._prohibition_(contract.getProhibition())
                ._provider_(contract.getProvider())
                .build();
    }

    /**
     * Deserialize string to ids rule.
     *
     * @param policy The policy string.
     * @return The ids rule.
     * @throws InvalidContractException If deserialization fails.
     */
    public Rule deserializeRule(final String policy) throws InvalidContractException {
        try {
            return serializerProvider.getSerializer().deserialize(policy, Rule.class);
        } catch (IOException exception) {
            LOGGER.warn("Could not deserialize rule. [exception=({})]", exception.getMessage());
            throw new InvalidContractException("Could not deserialize rule.");
        }
    }

    /**
     * Deserialize string to ids contract.
     *
     * @param contract The contract string.
     * @return The ids contract.
     * @throws InvalidContractException If deserialization fails.
     */
    public Contract deserializeContract(final String contract) throws InvalidContractException {
        try {
            return serializerProvider.getSerializer().deserialize(contract, Contract.class);
        } catch (IOException exception) {
            LOGGER.warn("Could not deserialize contract. [exception=({})]", exception.getMessage());
            throw new InvalidContractException("Could not deserialize contract.");
        }
    }

    /**
     * Deserialize string to ids contract agreement.
     *
     * @param contract The contract string.
     * @return The ids contract agreement.
     * @throws InvalidContractException If deserialization fails.
     */
    public ContractAgreement deserializeContractAgreement(final String contract) throws InvalidContractException {
        try {
            return serializerProvider.getSerializer().deserialize(contract,
                    ContractAgreement.class);
        } catch (IOException exception) {
            LOGGER.warn("Could not deserialize agreement. [exception=({})]",
                    exception.getMessage());
            throw new InvalidContractException("Could not deserialize contract agreement.");
        }
    }

    /**
     * Deserialize string to ids contract request.
     *
     * @param contract The contract string.
     * @return The ids contract request.
     * @throws InvalidContractException If deserialization fails.
     */
    public ContractRequest deserializeContractRequest(final String contract) throws InvalidContractException {
        try {
            return serializerProvider.getSerializer().deserialize(contract, ContractRequest.class);
        } catch (IOException exception) {
            LOGGER.warn("Could not deserialize request. [exception=({})]", exception.getMessage());
            throw new InvalidContractException("Could not deserialize contract request.");
        }
    }

    /**
     * Deserialize string to ids contract offer.
     *
     * @param contract The contract string.
     * @return The ids contract offer.
     * @throws IOException If deserialization fails.
     */
    public ContractOffer deserializeContractOffer(final String contract) throws IOException {
        try {
            return serializerProvider.getSerializer().deserialize(contract, ContractOffer.class);
        } catch (IOException exception) {
            LOGGER.warn("Could not deserialize offer. [exception=({})]", exception.getMessage());
            throw new InvalidContractException("Could not deserialize contract offer.");
        }
    }
}
