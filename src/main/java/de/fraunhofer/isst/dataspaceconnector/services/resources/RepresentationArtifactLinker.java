package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.util.List;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Handles the relation between a representation and its artifacts.
 */
@Service
@NoArgsConstructor
public class RepresentationArtifactLinker extends BaseUniDirectionalLinkerService<Representation,
        Artifact, RepresentationService, ArtifactService> {
    @Override
    protected List<Artifact> getInternal(final Representation owner) {
        return owner.getArtifacts();
    }
}
