package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RepresentationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.utils.MappingUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.SelfLinkHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class EntityUpdateService {

    /**
     * Service for requested resources.
     */
    private final @NonNull ResourceService<RequestedResource, RequestedResourceDesc> requestService;

    /**
     * Service for representations.
     */
    private final @NonNull RepresentationService representationService;

    /**
     * Service for artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Update value of artifact.
     *
     * @param artifact The artifact.
     * @param data     The data string.
     */
    public void updateDataOfArtifact(final Artifact artifact, final String data) {
        final var desc = new ArtifactDesc();
        desc.setValue(data);

        artifactService.update(artifact.getId(), desc);
    }

    /**
     * Update value of artifact by artifact id.
     *
     * @param artifactId The artifact id.
     * @param data       The data string.
     */
    public void updateDataOfArtifact(final UUID artifactId, final String data) {
        final var desc = new ArtifactDesc();
        desc.setValue(data);

        artifactService.update(artifactId, desc);
    }

    /**
     * Update database resource.
     *
     * @param resource The ids resource.
     */
    public void updateResource(final Resource resource) {
        final var resourceId = resource.getId();

        final var resources = requestService.getAll(Pageable.unpaged());
        for (final var entity : resources) {
            final var entityId = entity.getId();
            final var remoteId = entity.getRemoteId();
            if (remoteId.equals(resourceId)) {
                final var template =
                        MappingUtils.fromIdsResource(resource);
                final var desc = template.getDesc();

                final var update = requestService.update(entityId, desc);
                final var uri = SelfLinkHelper.getSelfLink(update);
                if (log.isDebugEnabled()) {
                    log.debug("Updated resource: " + uri);
                }
            }
        }
    }

    /**
     * Update database representation that is known to the consumer.
     *
     * @param representation The ids representation.
     */
    public void updateRepresentation(final Representation representation) {
        final var representationId = representation.getId();

        final var representations = representationService.getAll(Pageable.unpaged());
        for (final var entity : representations) {
            final var entityId = entity.getId();
            final var remoteId = entity.getRemoteId();
            if (remoteId.equals(representationId)) {
                final var template =
                        MappingUtils.fromIdsRepresentation(representation);
                final var desc = template.getDesc();

                final var update = representationService.update(entityId, desc);
                final var uri = SelfLinkHelper.getSelfLink(update);
                if (log.isDebugEnabled()) {
                    log.debug("Updated representation: " + uri);
                }
            }
        }
    }

    /**
     * Update database artifact that is known to the consumer.
     *
     * @param artifact The ids artifact.
     * @return True if the artifact's data should be downloaded, false if not.
     */
    public Optional<Artifact> updateArtifact(final de.fraunhofer.iais.eis.Artifact artifact) {
        final var artifactId = artifact.getId();

        final var artifacts = artifactService.getAll(Pageable.unpaged());
        for (final var entity : artifacts) {
            final var entityId = entity.getId();
            final var remoteId = entity.getRemoteId();
            if (remoteId.equals(artifactId)) {
                final var automatedDownload = entity.isAutomatedDownload();
                final var remoteUrl = entity.getRemoteAddress();
                final var template =
                        MappingUtils.fromIdsArtifact(artifact, automatedDownload, remoteUrl);
                final var desc = template.getDesc();

                final var update = artifactService.update(entityId, desc);
                final var uri = SelfLinkHelper.getSelfLink(update);
                if (log.isDebugEnabled()) {
                    log.debug("Updated artifact: " + uri);
                }

                return Optional.of(entity);
            }
        }

        return Optional.empty();
    }

    /**
     * Set confirmed boolean to true.
     *
     * @param agreement The database agreement.
     */
    public void updateAgreementToConfirmed(final Agreement agreement) {
        // TODO Get desc + update agreement to confirmed = true
    }
}
