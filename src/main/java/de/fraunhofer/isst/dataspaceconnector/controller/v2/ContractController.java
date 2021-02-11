package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.ContractView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.CommonService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/contracts")
class ContractController extends BaseResourceController<Contract, ContractDesc, ContractView,
        CommonService<Contract, ContractDesc, ContractView>> {
}
