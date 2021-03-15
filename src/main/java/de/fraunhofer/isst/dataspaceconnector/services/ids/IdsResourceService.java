package de.fraunhofer.isst.dataspaceconnector.services.ids;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.ids.IdsViewer;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.EndpointService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IdsResourceService {

    @Autowired
    private ResourceService<OfferedResource, OfferedResourceDesc> offeredResourceService;

    @Autowired
    private ResourceService<RequestedResource, RequestedResourceDesc> requestedResourceService;

    @Autowired
    private EndpointService endpointService;

    @Autowired
    private IdsViewer viewer;

    public de.fraunhofer.iais.eis.Resource getResource(final EndpointId endpointId) {
        final var endpoint = endpointService.get(endpointId);
        return viewer.create(offeredResourceService.get(endpoint.getInternalId()));
    }

    public de.fraunhofer.iais.eis.Resource getResource(final URI resourceUri) {
        final var allEndpoints = endpointService.getAll();
        for (final var endpoint : allEndpoints) {
            if (endpoint.toUri().equals(resourceUri)) {
                return getResource(endpoint);
            }
        }

        throw new RuntimeException("Not implemented");
    }

    // TODO Check if the id is the internal or the external one
    // TODO add exception handling
    public List<Resource> getAllOfferedResources() {
        return offeredResourceService.getAll(Pageable.unpaged())
                .stream()
                .map(viewer::create)
                .collect(Collectors.toList());
    }

    public List<Resource> getAllRequestedResources() {
        return requestedResourceService.getAll(Pageable.unpaged())
                .stream()
                .map(viewer::create)
                .collect(Collectors.toList());
    }

    /**
     * Get offered resource by its id.
     *
     * @param resourceId The resource id.
     * @return The ids resource.
     */
    public Resource getOfferedResourceById(final URI resourceId) {
        final var resource = offeredResourceService.getAll(Pageable.unpaged())
                .stream()
                .filter(x -> x.getId().toString().contains(resourceId.toString()))
                .findAny();

        return resource.map(offeredResource -> viewer.create(offeredResource)).orElse(null);
    }

    /**
     * Get requested resource by its id.
     *
     * @param resourceId The resource id.
     * @return The ids resource.
     */
    public Resource getRequestedResourceById(final URI resourceId) {
        final var resource = requestedResourceService.getAll(Pageable.unpaged())
                .stream()
                .filter(x -> x.getId().toString().contains(resourceId.toString()))
                .findAny();

        return resource.map(offeredResource -> viewer.create(offeredResource)).orElse(null);
    }
}
