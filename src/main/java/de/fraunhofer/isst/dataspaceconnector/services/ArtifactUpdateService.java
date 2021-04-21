package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.utils.MappingUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ArtifactUpdateService implements InfomodelUpdater<de.fraunhofer.iais.eis.Artifact, Artifact> {

    /**
     * Service for artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    @Override
    public Artifact update(final de.fraunhofer.iais.eis.Artifact entity) throws ResourceNotFoundException {
        final var entityId = artifactService.identifyByRemoteId(entity.getId());
        if (entityId.isEmpty()) {
            throw new ResourceNotFoundException(entity.getId().toString());
        }

        final var isAutoDownload = artifactService.get(entityId.get()).isAutomatedDownload();
        final var template = MappingUtils.fromIdsArtifact(entity, isAutoDownload);
        return artifactService.update(entityId.get(), template.getDesc());
    }
}
