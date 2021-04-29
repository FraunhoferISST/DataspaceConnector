package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.util.List;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Base class for handling resource-contract relations.
 * @param <T> The resource type.
 */
@NoArgsConstructor
public abstract class AbstractResourceContractLinker<T extends Resource>
        extends OwningRelationService<T, Contract, ResourceService<T, ?>,
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
 * Handles the relation between an offered resource and its contracts.
 */
@Service
@NoArgsConstructor
class OfferedResourceContractLinker extends AbstractResourceContractLinker<OfferedResource> { }

/**
 * Handles the relation between a requested resource and its contracts.
 */
@Service
@NoArgsConstructor
class RequestedResourceContractLinker extends AbstractResourceContractLinker<RequestedResource> { }
