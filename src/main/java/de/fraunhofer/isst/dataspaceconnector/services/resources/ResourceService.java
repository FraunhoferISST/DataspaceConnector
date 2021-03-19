package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceDesc;
import org.springframework.stereotype.Service;

public class ResourceService<T extends Resource, D extends ResourceDesc<T>> extends BaseEntityService<T, D> {
}

@Service
class OfferedResourceService extends ResourceService<OfferedResource, OfferedResourceDesc> {
}

@Service
class RequestedResourceService extends ResourceService<RequestedResource, RequestedResourceDesc> {
}
