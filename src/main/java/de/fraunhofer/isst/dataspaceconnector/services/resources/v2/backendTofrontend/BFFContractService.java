package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendTofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.ContractView;
import org.springframework.stereotype.Service;

@Service
class BFFContractService extends CommonService<Contract, ContractDesc,
        ContractView> {
}
