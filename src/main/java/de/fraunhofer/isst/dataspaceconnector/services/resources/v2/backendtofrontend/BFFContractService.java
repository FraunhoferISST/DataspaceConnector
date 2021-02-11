package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.ContractView;
import org.springframework.stereotype.Service;

@Service
public class BFFContractService extends CommonService<Contract, ContractDesc,
        ContractView> {
}
