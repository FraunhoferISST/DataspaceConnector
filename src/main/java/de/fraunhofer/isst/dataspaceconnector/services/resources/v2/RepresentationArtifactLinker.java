package de.fraunhofer.isst.dataspaceconnector.services.resources.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.v2.RepresentationDesc;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class RepresentationArtifactLinker
        extends BaseUniDirectionalLinkerService<Representation,
        RepresentationDesc, Artifact, ArtifactDesc, RepresentationService,
        ArtifactService> {
    @Override
    protected Map<UUID, Artifact> getInternal(final Representation owner) {
        return owner.getArtifacts();
    }
}
