package de.fraunhofer.isst.dataspaceconnector.services;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RelationshipServices;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.SelfLinkHelper;
import de.fraunhofer.isst.dataspaceconnector.utils.UUIDUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class EntityUpdateService {

    /**
     * Updates a requested resource by using an infomodel resource.
     */
    private final @NonNull RequestedResourceUpdater requestedResourceUpdater;

    /**
     * Updates a representation by using an infomodel representations.
     */
    private final @NonNull RepresentationUpdater representationUpdater;

    /**
     * Updates an artifact by using an infomodel artifacts.
     */
    private final @NonNull ArtifactUpdater artifactUpdater;

    /**
     * Service for agreements.
     */
    private final @NonNull AgreementService agreementService;

    /**
     * Service for linking artifacts to agreement.
     */
    private final @NonNull RelationshipServices.AgreementArtifactLinker agreementArtifactLinker;

    /**
     * Update database resource.
     * @param resource The ids resource.
     */
    public void updateResource(final Resource resource) {
        try {
            final var updated = requestedResourceUpdater.update(resource);
            log.debug("Updated resource. [uri=({})]", SelfLinkHelper.getSelfLink(updated));
        } catch (ResourceNotFoundException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to update resource. The resource could not be found. [uri=({})]",
                          resource.getId());
            }
        }
    }

    /**
     * Update database representation that is known to the consumer.
     * @param representation The ids representation.
     */
    public void updateRepresentation(final Representation representation) {
        try {
            final var updated = representationUpdater.update(representation);
            log.debug("Updated representation. [uri=({})]", SelfLinkHelper.getSelfLink(updated));
        } catch (ResourceNotFoundException exception) {
            if (log.isDebugEnabled()) {
                log.debug(
                        "Failed to update representation. The resource could not be found. [uri="
                        + "({})]",
                        representation.getId());
            }
        }
    }

    /**
     * Update database artifact that is known to the consumer.
     * @param artifact The ids artifact.
     * @return True if the artifact's data should be downloaded, false if not.
     */
    public Optional<Artifact> updateArtifact(final de.fraunhofer.iais.eis.Artifact artifact) {
        try {
            final var updated = artifactUpdater.update(artifact);
            log.debug("Updated artifact. [uri=({})]", SelfLinkHelper.getSelfLink(updated));
            return Optional.of(updated);
        } catch (ResourceNotFoundException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to update artifact. The resource could not be found. [uri=({})]",
                          artifact.getId());
            }
        }

        return Optional.empty();
    }

    /**
     * Set confirmed boolean to true.
     * @param agreement The database agreement.
     * @return true if the agreement has been confirmed.
     */
    public boolean confirmAgreement(final Agreement agreement) {
        try {
            return agreementService.confirmAgreement(agreement);
        } catch (ResourceNotFoundException exception) {
            if (log.isDebugEnabled()) {
                log.debug(
                        "Failed to confirm agreement. The resource could not be found. [uri=({})]",
                        agreement.getId());
            }

            return false;
        }
    }

    // TODO link agreement to artifacts.
    public final void linkArtifactToAgreement(final List<URI> artifactIds, final URI agreementId) {
        final var agreementUuid = EndpointUtils.getUUIDFromPath(agreementId);
        agreementArtifactLinker.add(agreementUuid, toSet(artifactIds));
    }

    private static Set<UUID> toSet(final List<URI> uris) {
        // NOTE Yes this is duplicate from the Resource Controller
        return uris.parallelStream().map(UUIDUtils::uuidFromUri).collect(Collectors.toSet());
    }
}
