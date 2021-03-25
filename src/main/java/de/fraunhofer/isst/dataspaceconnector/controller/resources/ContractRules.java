package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.view.ContractRuleView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ContractRuleLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contracts/{id}/rules")
@Tag(name = "Contracts", description = "Endpoints for linking rules to contracts")
public class ContractRules extends BaseResourceChildController<ContractRuleLinker, ContractRule, ContractRuleView> {
}
