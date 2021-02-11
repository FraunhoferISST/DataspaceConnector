package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.RuleView;
import org.springframework.stereotype.Service;

@Service
public class RuleBFFService extends CommonService<ContractRule, ContractRuleDesc, RuleView> {
}
