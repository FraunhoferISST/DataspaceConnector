package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractBuilderException;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

@Service
@RequiredArgsConstructor
public class IdsContractService {

    /**
     * Service for ids serializations.
     */
    private final @NonNull SerializerProvider serializerProvider;

    /**
     * The current connector configuration.
     */
    private final @NonNull ConfigurationContainer configurationContainer;

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
     * @param contract The contract.
     * @param contractId The id of the contract.
     * @return The contract agreement.
     */
    public ContractAgreement buildAgreementFromContract(final Contract contract, final URI contractId) throws ContractBuilderException {
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
     * Deserialize string to ids contract.
     *
     * @param policy The policy string.
     * @return The ids contract.
     * @throws IOException If deserialization fails.
     */
    public Contract deserializeContract(final String policy) throws IOException {
        return serializerProvider.getSerializer().deserialize(policy, Contract.class);
    }

    /**
     * Deserialize string to ids rule.
     *
     * @param policy The policy string.
     * @return The ids rule.
     * @throws IOException If deserialization fails.
     */
    public Rule deserializeRule(final String policy) throws IOException {
        return serializerProvider.getSerializer().deserialize(policy, Rule.class);
    }
}
