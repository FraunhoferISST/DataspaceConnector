package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.ContractView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ContractService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contracts")
@Tag(name = "Contracts", description = "Endpoints for CRUD operations on contracts")
public class ContractController extends BaseResourceController<Contract, ContractDesc, ContractView, ContractService> {
}
