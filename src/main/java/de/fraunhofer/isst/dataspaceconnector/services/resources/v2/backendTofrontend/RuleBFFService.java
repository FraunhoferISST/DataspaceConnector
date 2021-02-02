package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendTofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Rule;
import de.fraunhofer.isst.dataspaceconnector.model.v2.RuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.RuleView;
import org.springframework.stereotype.Service;

@Service
class RuleBFFService extends CommonService<Rule, RuleDesc, RuleView> {
}
