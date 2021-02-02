package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Rule;
import de.fraunhofer.isst.dataspaceconnector.model.v2.RuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.RuleView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendTofrontend.CommonService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rules")
class RuleController extends BaseResourceController<Rule, RuleDesc, RuleView,
        CommonService<Rule, RuleDesc, RuleView>> {
}
