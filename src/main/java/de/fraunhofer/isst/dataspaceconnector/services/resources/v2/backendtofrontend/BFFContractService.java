package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.ContractView;
import org.springframework.stereotype.Service;

@Service
public class BFFContractService extends CommonService<Contract, ContractDesc,
        ContractView> {
}
