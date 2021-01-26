package de.fraunhofer.isst.dataspaceconnector.services.resources.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.v2.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ResourceDesc;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ResourceRepresentationLinker
        extends BaseUniDirectionalLinkerService<Resource, ResourceDesc,
        Representation, RepresentationDesc, ResourceService,
        RepresentationService> {
    @Override
    protected Map<UUID, Representation> getInternal(final Resource owner) {
        return owner.getRepresentations();
    }
}
