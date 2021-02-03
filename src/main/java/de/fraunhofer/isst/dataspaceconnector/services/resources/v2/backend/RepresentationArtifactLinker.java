package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Representation;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class RepresentationArtifactLinker
        extends BaseUniDirectionalLinkerService<Representation, Artifact,
        RepresentationService, ArtifactService> {

    @Override
    protected Map<UUID, Artifact> getInternal(final Representation owner) {
        return owner.getArtifacts();
    }
}
