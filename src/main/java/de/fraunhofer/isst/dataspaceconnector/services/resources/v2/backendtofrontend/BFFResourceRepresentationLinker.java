package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceRepresentationLinker;
import org.springframework.stereotype.Service;

public class BFFResourceRepresentationLinker<T extends Resource> extends CommonUniDirectionalLinkerService<ResourceRepresentationLinker<T>> {
}

@Service
final class BFFOfferedResourceRepresentationLinker extends BFFResourceRepresentationLinker<OfferedResource> {

}

@Service
final class BFFRequestedResourceRepresentationLinker extends BFFResourceRepresentationLinker<RequestedResource> {

}
