package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractRule;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ContractRuleLinker extends BaseUniDirectionalLinkerService<
        Contract, ContractRule, ContractService, RuleService> {

    @Override
    protected Map<UUID, ContractRule> getInternal(final Contract owner) {
        return owner.getRules();
    }
}
