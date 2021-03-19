package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepresentationArtifactLinker
        extends BaseUniDirectionalLinkerService<Representation, Artifact,
        RepresentationService, ArtifactService> {

    @Override
    protected List<Artifact> getInternal(final Representation owner) {
        return owner.getArtifacts();
    }
}
