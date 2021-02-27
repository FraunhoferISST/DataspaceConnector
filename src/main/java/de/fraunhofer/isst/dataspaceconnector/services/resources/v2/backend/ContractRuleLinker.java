package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractRuleLinker extends BaseUniDirectionalLinkerService<
        Contract, ContractRule, ContractService, RuleService> {

    @Override
    protected List<ContractRule> getInternal(final Contract owner) {
        return owner.getRules();
    }
}
