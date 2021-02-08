package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ContractRuleLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.CommonUniDirectionalLinkerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/contracts/{id}/rules")
public class ContractRules extends BaseResourceChildController<CommonUniDirectionalLinkerService<ContractRuleLinker>> {
}
