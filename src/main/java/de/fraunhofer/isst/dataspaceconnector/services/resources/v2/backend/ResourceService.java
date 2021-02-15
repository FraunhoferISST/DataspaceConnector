package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceDesc;
import org.springframework.stereotype.Service;

public class ResourceService<T extends Resource, D extends ResourceDesc<T>> extends BaseService<T, D> {
}

@Service
final class OfferedResourceService extends ResourceService<OfferedResource, OfferedResourceDesc> {
}

@Service
final class RequestedResourceService extends ResourceService<RequestedResource, RequestedResourceDesc> {
}
