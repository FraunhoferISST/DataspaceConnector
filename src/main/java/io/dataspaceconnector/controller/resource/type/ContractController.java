package io.dataspaceconnector.controller.resource.type;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.controller.resource.base.BaseResourceController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.contract.ContractView;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.contract.ContractDesc;
import io.dataspaceconnector.service.resource.type.ContractService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing contracts.
 */
@RestController
@RequestMapping(BasePath.CONTRACTS)
@Tag(name = ResourceName.CONTRACTS, description = ResourceDescription.CONTRACTS)
public class ContractController
        extends BaseResourceController<Contract, ContractDesc, ContractView, ContractService> {
}
