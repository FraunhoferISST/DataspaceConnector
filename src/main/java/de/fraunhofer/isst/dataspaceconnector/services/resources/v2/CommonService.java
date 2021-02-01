package de.fraunhofer.isst.dataspaceconnector.services.resources.v2;

import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceAlreadyExistsException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceMovedException;
import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseDescription;
import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseResource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.v2.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.v2.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

/**
 * Handles exposing resource functions to controllers.
 *
 * @param <T> The resource type.
 * @param <D> The description for the passed resource type.
 */
public class CommonService<T extends BaseResource,
        D extends BaseDescription<T>> implements FrontFacingService<T, D> {

    /**
     * The service for resources.
     **/
    @Autowired
    private BaseService<T, D> resourceService;

    /**
     * The service for endpoints.
     **/
    @Autowired
    private EndpointService endpointService;

    /**
     * Create a resource at a passed resource path.
     *
     * @param basePath The path leading to the resource.
     * @param desc     The resource description.
     * @return The endpoint where the new resource is provided.
     */
    @Override
    public EndpointId create(final String basePath, final D desc) {
        final var resource = resourceService.create(desc);

        return endpointService.create(new EndpointId(basePath,
                generateEndpointResourceId(new EndpointId(basePath,
                        desc.getStaticId()))), resource.getId()).getId();
    }

    /**
     * Update a resource provided at a given endpoint.
     *
     * @param endpointId The endpoint of the resource.
     * @param desc       The new updated resource description.
     * @return The endpoint where the updated resource is provided.
     */
    @Override
    public EndpointId update(final EndpointId endpointId, final D desc) {
        var resource = get(endpointId);

        // Update the underlying resource
        resourceService.update(resource.getId(), desc);

        // Move the resource and create new endpoint if necessary
        if (desc.getStaticId() != null
                && endpointId.getResourceId() != desc.getStaticId()) {
            // The resource needs to be moved.
            var newEndpoint = endpointService.create(
                    new EndpointId(endpointId.getBasePath(),
                            desc.getStaticId()), resource.getId());

            // Mark the old resource as moved
            endpointService.update(endpointId, newEndpoint.getId());

            return newEndpoint.getId();
        }

        return endpointId;
    }

    /**
     * Get the resource at a given endpoint.
     *
     * @param endpointId The endpoint of the resource.
     * @return The resource.
     */
    @Override
    public T get(final EndpointId endpointId) {
        var endpoint = getEndpoint(endpointId);

        if (endpoint.getInternalId() == null) {
            // Handle with global exception handler
            throw new ResourceMovedException(endpoint.getNewLocation());
        } else {
            return resourceService.get(endpoint.getInternalId());
        }
    }

    @Override
    public Endpoint getEndpoint(final EndpointId endpointId) {
        // TODO Is this function really needed? Should it be provided?
        return endpointService.get(endpointId);
    }

    /**
     * Get all available endpoints.
     *
     * @return All endpoints.
     */
    @Override
    public Set<EndpointId> getAll() {
        return endpointService.getAll();
    }

    /**
     * Checks if an endpoint exists.
     *
     * @param endpointId The endpoint.
     * @return True if the endpoint exists.
     */
    @Override
    public boolean doesExist(final EndpointId endpointId) {
        return endpointService.doesExist(endpointId);
    }

    @Override
    public void delete(final EndpointId endpointId) {
        var resource = get(endpointId);
        endpointService.delete(endpointId);

        // TODO: Define what should happens here. Will the resource move to
        //  one of the referencing endpoints or will all endpoints pointing
        //  here be deleted?
    }


    private UUID generateEndpointResourceId(final EndpointId id) {
        // TODO: FIX ME
        // TODO what happends when basePath is not set
        if (id.getResourceId() != null) {
            if (doesExist(id)) {
                throw new ResourceAlreadyExistsException(id.toString());
            }

            // Preferred endpoint available
            return id.getResourceId();
        } else {
            // No endpoint hint
            return UUIDUtils.createUUID(x ->
                    doesExist(new EndpointId(id.getBasePath(), x)));
        }
    }
}

@Service
class BFFCatalogService extends CommonService<Catalog, CatalogDesc> {
}

@Service
class BFFResourceService extends CommonService<Resource, ResourceDesc> {
}

@Service
class BFFRepresentationService extends CommonService<Representation, RepresentationDesc> {
}

@Service
class BFFContractService extends CommonService<Contract, ContractDesc> {
}
