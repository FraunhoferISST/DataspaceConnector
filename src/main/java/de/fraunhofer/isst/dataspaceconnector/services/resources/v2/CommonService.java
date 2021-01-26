package de.fraunhofer.isst.dataspaceconnector.services.resources.v2;

import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceAlreadyExistsException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceMovedException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseDescription;
import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseFactory;
import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseResource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.repositories.v2.BaseResourceRepository;
import de.fraunhofer.isst.dataspaceconnector.repositories.v2.EndpointRepository;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommonService<T extends BaseResource,
        D extends BaseDescription<T>> implements BaseService<T, D> {

    @Autowired
    protected BaseResourceRepository<T> resourceRepository;

    @Autowired
    protected EndpointRepository endpointRepository;

    @Autowired
    private BaseFactory<T, D> factory;

    public T create(final D desc) {
        return persist(factory.create(desc));
    }

    public EndpointId create(final String basePath, final D desc) {
        var resource = create(desc);
        return createEndpoint(new EndpointId(basePath,
                generateEndpointResourceId(new EndpointId(basePath,
                        desc.getStaticId()))), resource.getId());
    }

    public T update(final UUID id, final D desc) {
        var resource = get(id);
        if (factory.update(resource, desc)) {
            resource = persist(resource);
        }

        return resource;
    }

    public EndpointId update(final EndpointId endpointId, final D desc) {
        var resource = get(endpointId);

        // Update the underlying resource
        if (factory.update(resource, desc)) {
            resource = persist(resource);
        }

        // Move the resource and create new endpoint if necessary
        if (desc.getStaticId() != null && endpointId.getResourceId() != desc.getStaticId()) {
            // The resource needs to be moved.
            var newEndpoint =
                    createEndpoint(new EndpointId(endpointId.getBasePath(),
                            desc.getStaticId()), resource.getId());

            // Mark the old resource as moved
            var oldEndpoint = getEndpoint(endpointId);
            oldEndpoint.setInternalId(null);
            oldEndpoint.setNewLocation(getEndpoint(newEndpoint));

            endpointRepository.saveAndFlush(oldEndpoint);

            return newEndpoint;
        }

        return endpointId;
    }

    public T get(final UUID id) {
        final var resource = resourceRepository.findById(id);

        if (resource.isEmpty())
        // Resource not available //TODO Needs exception handler
        {
            throw new ResourceNotFoundException(id.toString());
        }

        return resource.get();
    }

    public T get(final EndpointId endpointId) {
        var endpoint = getEndpoint(endpointId);

        if (endpoint.getInternalId() == null) {
            // Handle with global exception handler
            throw new ResourceMovedException(endpoint.getNewLocation());
        } else {
            return get(endpoint.getInternalId());
        }
    }

    public List<EndpointId> getAll() {
        var allEndpoints = endpointRepository.findAll();

        // TODO Replace with custom query
        var allEndpointIds = new ArrayList<EndpointId>();
        for (var endpoint : allEndpoints)
            allEndpointIds.add(endpoint.getId());

        return allEndpointIds;
    }

    public boolean doesExist(final UUID id) {
        return resourceRepository.findById(id).isPresent();
    }

    public boolean doesExist(final EndpointId endpointId) {
        return endpointRepository.findById(endpointId).isPresent();
    }

    public void delete(final EndpointId endpointId) {
        var resource = get(endpointId);
        endpointRepository.deleteById(endpointId);

        // TODO: Define what should happens here. Will the resource move to
        //  one of the referencing endpoints or will all endpoints pointing
        //  here be deleted?
    }

    T persist(T t) {
        return resourceRepository.saveAndFlush(t);
    }

    public Endpoint getEndpoint(final EndpointId endpointId) {
        var endpoint = endpointRepository.findById(endpointId);

        if (endpoint.isEmpty()) {
            // Handle with global exception handler
            throw new ResourceNotFoundException(endpoint.toString());
        }

        return endpoint.get();
    }

    private EndpointId createEndpoint(final EndpointId id,
                                      final UUID internalId) {
        var endpoint = new Endpoint();
        endpoint.setId(id);
        endpoint.setInternalId(internalId);

        endpoint = endpointRepository.saveAndFlush(endpoint);

        return endpoint.getId();
    }

    private UUID generateEndpointResourceId(final EndpointId id) {
        if (id != null) {
            if (doesExist(id)) {
                throw new ResourceAlreadyExistsException(id.toString());
            }

            return id.getResourceId();
        } else {
            return UUIDUtils.createUUID(x -> doesExist(new EndpointId(id.getBasePath(), x)));
        }
    }
}
