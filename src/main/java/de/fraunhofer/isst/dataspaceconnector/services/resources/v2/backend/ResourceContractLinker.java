package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

public class ResourceContractLinker<T extends Resource>
        extends BaseUniDirectionalLinkerService<T,
        Contract, ResourceService<T, ?>, ContractService> {

    @Override
    protected List<Contract> getInternal(final Resource owner) {
        return owner.getContracts();
    }
}

@Service
final class OfferedResourceContractLinker extends ResourceContractLinker<OfferedResource>{}

@Service
final class RequestedResourceContractLinker extends ResourceContractLinker<RequestedResource>{}
