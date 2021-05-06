package io.dataspaceconnector.services.resources;

import io.dataspaceconnector.model.ContractRule;
import io.dataspaceconnector.model.ContractRuleDesc;
import io.dataspaceconnector.repositories.RuleRepository;
import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.Utils;
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
        Utils.requireNonNull(contractId, ErrorMessages.ENTITYID_NULL);
        return ((RuleRepository) getRepository()).findAllByContract(contractId);
    }

}
