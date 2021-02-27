package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

public class ResourceRepresentationLinker<T extends Resource>
        extends BaseUniDirectionalLinkerService<T,
        Representation, ResourceService<T, ?>,
        RepresentationService> {

    @Override
    protected List<Representation> getInternal(final Resource owner) {
        return owner.getRepresentations();
    }
}

@Service
final class OfferedResourceRepresentation extends ResourceRepresentationLinker<OfferedResource> {
}

@Service
final class RequestedResourceRepresentation extends ResourceRepresentationLinker<RequestedResource> {
}
