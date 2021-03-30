package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.ContractRuleView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rules")
@Tag(name = "Rules", description = "Endpoints for CRUD operations on rules")
public class RuleController extends BaseResourceController<ContractRule, ContractRuleDesc, ContractRuleView, RuleService> {
}
