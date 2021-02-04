package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.RuleView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.CommonService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rules")
class RuleController extends BaseResourceController<ContractRule, ContractRuleDesc, RuleView,
        CommonService<ContractRule, ContractRuleDesc, RuleView>> {
}
