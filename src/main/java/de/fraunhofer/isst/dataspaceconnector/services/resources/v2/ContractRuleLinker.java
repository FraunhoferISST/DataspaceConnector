package de.fraunhofer.isst.dataspaceconnector.services.resources.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Rule;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ContractRuleLinker extends BaseUniDirectionalLinkerService<
        Contract, Rule, ContractService, RuleService> {

    @Override
    protected Map<UUID, Rule> getInternal(final Contract owner) {
        return owner.getRules();
    }
}
