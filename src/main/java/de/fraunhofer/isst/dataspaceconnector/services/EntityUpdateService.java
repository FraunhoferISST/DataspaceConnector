package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.services.ids.updater.ArtifactUpdater;
import de.fraunhofer.isst.dataspaceconnector.services.ids.updater.RepresentationUpdater;
import de.fraunhofer.isst.dataspaceconnector.services.ids.updater.RequestedResourceUpdater;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RelationServices;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.SelfLinkHelper;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class EntityUpdateService {

    /**
     * Updates a requested resource by using an ids resource.
     */
    private final @NonNull RequestedResourceUpdater requestedResourceUpdater;

    /**
     * Updates a representation by using an ids representations.
     */
    private final @NonNull RepresentationUpdater representationUpdater;

    /**
     * Updates an artifact by using an ids artifacts.
     */
    private final @NonNull ArtifactUpdater artifactUpdater;

    /**
     * Service for agreements.
     */
    private final @NonNull AgreementService agreementService;

    /**
     * Service for linking artifacts to agreement.
     */
    private final @NonNull RelationServices.AgreementArtifactLinker agreementArtifactLinker;

    /**
     * Service for artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Update database resource.
     *
     * @param resource The ids resource.
     */
    public void updateResource(final Resource resource) {
        try {
            final var updated = requestedResourceUpdater.update(resource);
            if (log.isDebugEnabled()) {
                log.debug("Updated resource. [uri=({})]", SelfLinkHelper.getSelfLink(updated));
            }

            final var representations = resource.getRepresentation();
            for (final var representation : Utils.requireNonNull(representations, ErrorMessages.LIST_NULL)) {
                updateRepresentation(representation);
            }
        } catch (ResourceNotFoundException | IllegalArgumentException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to update resource. [uri=({})]", resource.getId());
            }
        }
    }

    /**
     * Update database representation that is known to the consumer.
     *
     * @param representation The ids representation.
     */
    public void updateRepresentation(final Representation representation) {
        try {
            final var updated = representationUpdater.update(representation);
            if (log.isDebugEnabled()) {
                log.debug("Updated representation. [uri=({})]",
                        SelfLinkHelper.getSelfLink(updated));
            }

            final var artifacts = representation.getInstance();
            for (final var artifact : Utils.requireNonNull(artifacts, ErrorMessages.LIST_NULL)) {
                updateArtifact((Artifact) artifact);
            }
        } catch (ResourceNotFoundException | IllegalArgumentException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to update representation. [uri=({})]", representation.getId());
            }
        }
    }

    /**
     * Update database artifact that is known to the consumer.
     *
     * @param artifact The ids artifact.
     */
    public void updateArtifact(final Artifact artifact) {
        try {
            final var updated = artifactUpdater.update(artifact);
            log.debug("Updated artifact. [uri=({})]", SelfLinkHelper.getSelfLink(updated));
        } catch (ResourceNotFoundException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to update artifact. [uri=({})]", artifact.getId());
            }
        }
    }

    /**
     * Set confirmed boolean to true.
     *
     * @param agreement The database agreement.
     * @return true if the agreement has been confirmed.
     */
    public boolean confirmAgreement(final Agreement agreement) {
        try {
            return agreementService.confirmAgreement(agreement);
        } catch (ResourceNotFoundException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to confirm agreement. [uri=({})]", agreement.getId());
            }

            return false;
        }
    }

    /**
     * Link list of artifacts to a contract agreement.
     *
     * @param artifactIds List of artifact ids.
     * @param agreementId The id of the agreement.
     */
    public final void linkArtifactToAgreement(final List<URI> artifactIds, final UUID agreementId) {
        final var localArtifacts = Utils.toStream(artifactIds)
                .map(x -> artifactService.identifyByRemoteId(x).get()).collect(Collectors.toSet());
        agreementArtifactLinker.add(agreementId, localArtifacts);
    }
}
