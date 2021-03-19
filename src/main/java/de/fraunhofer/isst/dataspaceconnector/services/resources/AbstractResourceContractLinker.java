package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.util.List;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The base class for linking contracts to resources.
 * @param <T> The resource type.
 */
@NoArgsConstructor
public abstract class AbstractResourceContractLinker<T extends Resource>
        extends BaseUniDirectionalLinkerService<T, Contract, ResourceService<T, ?>,
                ContractService> {
    /**
     * Get the list of contracts owned by the resource.
     * @param owner The owner of the contracts.
     * @return The list of owned contracts.
     */
    @Override
    protected List<Contract> getInternal(final Resource owner) {
        return owner.getContracts();
    }
}

/**
 * Links contracts to offered resources.
 */
@Service
@NoArgsConstructor
class OfferedResourceContractLinker extends AbstractResourceContractLinker<OfferedResource> { }

/**
 * Links contracts to requested resources.
 */
@Service
@NoArgsConstructor
class RequestedResourceContractLinker extends AbstractResourceContractLinker<RequestedResource> { }
