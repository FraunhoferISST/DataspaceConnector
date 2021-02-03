package de.fraunhofer.isst.dataspaceconnector.model.v2;

import de.fraunhofer.isst.dataspaceconnector.services.utils.MetaDataUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Creates and updates a contract.
 */
@Component
public class ContractFactory implements BaseFactory<Contract, ContractDesc> {

    /**
     * Create a new contract.
     *
     * @param desc The description of the new contract.
     * @return The new contract.
     */
    @Override
    public Contract create(final ContractDesc desc) {
        final var contract = new Contract();
        contract.setRules(new HashMap<>());

        update(contract, desc);

        return contract;
    }

    /**
     * Update a contract.
     *
     * @param contract The contract to be updated.
     * @param desc     The new contract description.
     * @return True if the contract has been modified.
     */
    @Override
    public boolean update(final Contract contract, final ContractDesc desc) {
        return this.updateTitle(contract, desc.getTitle());
    }

    private boolean updateTitle(final Contract contract, final String title) {
        final var newTitle = MetaDataUtils.updateString(contract.getTitle(),
                title, "");
        newTitle.ifPresent(contract::setTitle);

        return newTitle.isPresent();
    }
}
