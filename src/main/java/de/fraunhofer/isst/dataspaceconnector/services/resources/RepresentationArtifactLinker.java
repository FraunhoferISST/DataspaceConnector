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
    /**
     * Get the list of artifacts owned by the representation.
     * @param owner The owner of the artifacts.
     * @return The list of owned artifacts.
     */
    @Override
    protected List<Artifact> getInternal(final Representation owner) {
        return owner.getArtifacts();
    }
}
