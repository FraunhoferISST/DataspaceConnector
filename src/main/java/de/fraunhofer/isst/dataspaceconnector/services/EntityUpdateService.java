package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RepresentationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.MappingUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntityUpdateService {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityUpdateService.class);

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

        final var artifactId = artifact.getId();
        artifactService.update(artifactId, desc);
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
            final var remoteId = URI.create(""); // TODO entity.getRemoteId();
            if (remoteId.equals(resourceId)) {
                final var template =
                        MappingUtils.fromIdsResource(resource);
                final var desc = template.getDesc();

                final var update = requestService.update(entityId, desc);
                final var uri = EndpointUtils.getSelfLink(update);
                LOGGER.info("Updated resource: " + uri);
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
                final var uri = EndpointUtils.getSelfLink(update);
                LOGGER.info("Updated representation: " + uri);
            }
        }
    }

    /**
     * Update database artifact that is known to the consumer.
     *
     * @param artifact The ids artifact.
     */
    public void updateArtifact(final de.fraunhofer.iais.eis.Artifact artifact) {
        final var artifactId = artifact.getId();

        final var artifacts = artifactService.getAll(Pageable.unpaged());
        for (final var entity : artifacts) {
            final var entityId = entity.getId();
            final var remoteId = entity.getRemoteId();
            if (remoteId.equals(artifactId)) {
                final var automatedDownload = entity.isAutomatedDownload();
                final var template =
                        MappingUtils.fromIdsArtifact(artifact, automatedDownload);
                final var desc = template.getDesc();

                final var update = artifactService.update(entityId, desc);
                final var uri = EndpointUtils.getSelfLink(update);
                LOGGER.info("Updated artifact: " + uri);
            }
        }
    }
}
