package de.fraunhofer.isst.dataspaceconnector.model;

import de.fraunhofer.isst.dataspaceconnector.utils.MetadataUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;

/**
 * Creates and updates a contract.
 */
@Component
public class ContractFactory implements AbstractFactory<Contract, ContractDesc> {

    /**
     * Default constructor.
     */
    public ContractFactory() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Create a new contract.
     *
     * @param desc The description of the new contract.
     * @return The new contract.
     */
    @Override
    public Contract create(final ContractDesc desc) {
        final var contract = new Contract();
        contract.setRules(new ArrayList<>());

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
        final var updateTitle = this.updateTitle(contract, desc.getTitle());
        final var updateStart = this.updateStart(contract, desc.getStart());
        final var updateEnd = this.updateEnd(contract, desc.getEnd());

        return updateTitle || updateStart || updateEnd;
    }

    private boolean updateTitle(final Contract contract, final String title) {
        final var newTitle = MetadataUtils.updateString(contract.getTitle(),
                title, "");
        newTitle.ifPresent(contract::setTitle);

        return newTitle.isPresent();
    }

    private boolean updateStart(final Contract contract, final Date start) {
        final var newStart = MetadataUtils.updateDate(contract.getStart(), start, new Date());
        newStart.ifPresent(contract::setStart);

        return newStart.isPresent();
    }

    private boolean updateEnd(final Contract contract, final Date end) {
        final var newEnd = MetadataUtils.updateDate(contract.getEnd(), end, new Date());
        newEnd.ifPresent(contract::setEnd);

        return newEnd.isPresent();
    }
}
