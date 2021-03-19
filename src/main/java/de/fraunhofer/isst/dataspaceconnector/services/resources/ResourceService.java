package de.fraunhofer.isst.dataspaceconnector.services.resources;

import org.springframework.stereotype.Service;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceDesc;
import lombok.NoArgsConstructor;

/**
 * Handles the basic logic for resources.
 * @param <T> The resource type.
 * @param <D> The resource description type.
 */
@NoArgsConstructor
public class ResourceService<T extends Resource, D extends ResourceDesc<T>>
        extends BaseEntityService<T, D> { }

/**
 * Handles the basic logic for offered resources.
 */
@Service
@NoArgsConstructor
class OfferedResourceService extends ResourceService<OfferedResource, OfferedResourceDesc> { }

/**
 * Handles the basic logic for requested resources.
 */
@Service
@NoArgsConstructor
class RequestedResourceService extends ResourceService<RequestedResource, RequestedResourceDesc> { }
