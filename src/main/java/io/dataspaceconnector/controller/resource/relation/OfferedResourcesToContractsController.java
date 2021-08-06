package io.dataspaceconnector.controller.resource.relation;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.base.BaseResourceChildController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.contract.ContractView;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.service.resource.relation.AbstractResourceContractLinker;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing the relations between offered resources and contracts.
 */
@RestController
@RequestMapping(BasePath.OFFERS + "/{id}/" + BaseType.CONTRACTS)
@Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
public class OfferedResourcesToContractsController extends BaseResourceChildController<
        AbstractResourceContractLinker<OfferedResource>, Contract, ContractView> {
}
