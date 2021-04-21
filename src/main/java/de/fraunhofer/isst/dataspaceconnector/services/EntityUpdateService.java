package de.fraunhofer.isst.dataspaceconnector.services;

import java.util.UUID;

import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.services.resources.AgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.utils.SelfLinkHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class EntityUpdateService {

    private final @NonNull RequestedResourceUpdateService requestedResourceUpdater;

    private final @NonNull RepresentationUpdateService representationUpdateService;

    private final @NonNull ArtifactUpdateService artifactUpdateService;

    private final @NonNull ArtifactService artifactService;

    private final @NonNull AgreementService agreementService;

    /**
     * Update value of artifact.
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
            final var updated = representationUpdateService.update(representation);
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
     */
    public void updateArtifact(final de.fraunhofer.iais.eis.Artifact artifact) {
        try {
            final var updated = artifactUpdateService.update(artifact);
            log.debug("Updated artifact. [uri=({})]", SelfLinkHelper.getSelfLink(updated));
        } catch (ResourceNotFoundException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to update artifact. The resource could not be found. [uri=({})]",
                          artifact.getId());
            }
        }
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
}
