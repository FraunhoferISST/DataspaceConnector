package io.dataspaceconnector.controller.resource.relation;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.base.BaseResourceChildController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.contract.ContractView;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.service.resource.relation.RuleContractLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing the relations between rules and contracts.
 */
@RestController
@RequestMapping(BasePath.RULES + "/{id}/" + BaseType.CONTRACTS)
@Tag(name = ResourceName.RULES, description = ResourceDescription.RULES)
public class RulesToContractsController extends BaseResourceChildController<
        RuleContractLinker, Contract, ContractView> {
}
