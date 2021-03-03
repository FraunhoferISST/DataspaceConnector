package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractBuilderException;

import java.net.URI;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Contains util methods for contract handling.
 */
public final class ContractUtils {

    /**
     * Build the contract request.
     *
     * @param contract The contract.
     * @return The contract request.
     */
    public static ContractRequest buildContractRequest(final Contract contract, final Connector connector) throws ContractBuilderException {
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
    public static ContractAgreement buildContractAgreement(final Contract contract) throws ContractBuilderException {
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
    public static ContractAgreement buildContractAgreement(final Contract contract, final URI contractId) throws ContractBuilderException {
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
}
