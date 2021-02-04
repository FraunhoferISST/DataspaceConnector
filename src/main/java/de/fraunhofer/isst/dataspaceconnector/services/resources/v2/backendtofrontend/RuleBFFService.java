package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.RuleView;
import org.springframework.stereotype.Service;

@Service
class RuleBFFService extends CommonService<ContractRule, ContractRuleDesc, RuleView> {
}
