package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.ContractView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.ContractRuleLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendTofrontend.CommonService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendTofrontend.CommonUniDirectionalLinkerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contracts")
class ContractController extends BaseResourceController<Contract, ContractDesc, ContractView,
        CommonService<Contract, ContractDesc, ContractView>> {
}

@RestController
@RequestMapping("/contracts/{id}/rules")
class ContractRules extends BaseResourceChildController<CommonUniDirectionalLinkerService<ContractRuleLinker>> {
}
