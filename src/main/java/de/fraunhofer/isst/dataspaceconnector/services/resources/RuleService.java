package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.repositories.RuleRepository;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Handles the basic logic for contract rules.
 */
@Service
@NoArgsConstructor
public class RuleService extends BaseEntityService<ContractRule, ContractRuleDesc> {

    /**
     * Finds all rules in a specific contract.
     *
     * @param contractId ID of the contract
     * @return list of all rules in the contract
     */
    public List<ContractRule> getAllByContract(final UUID contractId) {
        return ((RuleRepository) getRepository()).findAllByContract(contractId);
    }

}
