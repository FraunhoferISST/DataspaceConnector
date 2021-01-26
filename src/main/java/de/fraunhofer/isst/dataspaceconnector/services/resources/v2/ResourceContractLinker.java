package de.fraunhofer.isst.dataspaceconnector.services.resources.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ResourceDesc;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ResourceContractLinker
        extends BaseUniDirectionalLinkerService<Resource, ResourceDesc,
        Contract, ContractDesc, ResourceService, ContractService> {
    @Override
    protected Map<UUID, Contract> getInternal(final Resource owner) {
        return owner.getContracts();
    }
}
