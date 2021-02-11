package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ResourceContractLinker
        extends BaseUniDirectionalLinkerService<Resource,
        Contract, ResourceService, ContractService> {

    @Override
    protected Map<UUID, Contract> getInternal(final Resource owner) {
        return owner.getContracts();
    }
}
