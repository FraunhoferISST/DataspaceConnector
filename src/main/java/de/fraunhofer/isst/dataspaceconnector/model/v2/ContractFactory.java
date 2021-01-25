package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ContractFactory implements BaseFactory<Contract, ContractDesc> {
    @Override
    public Contract create(final ContractDesc desc) {
        var contract = new Contract();
        contract.setRules(new HashMap<>());

        update(contract, desc);

        return contract;
    }

    @Override
    public boolean update(final Contract contract, final ContractDesc desc) {
        var hasBeenUpdated = false;

        var newTitle = desc.getTitle() != null ? desc.getTitle() : "";
        if (newTitle.equals(contract.getTitle())) {
            contract.setTitle(newTitle);
            hasBeenUpdated = true;
        }

        return hasBeenUpdated;
    }
}
