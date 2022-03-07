/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.service;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.service.resource.ids.updater.ArtifactUpdater;
import io.dataspaceconnector.service.resource.ids.updater.RepresentationUpdater;
import io.dataspaceconnector.service.resource.ids.updater.RequestedResourceUpdater;
import io.dataspaceconnector.service.resource.relation.AgreementArtifactLinker;
import io.dataspaceconnector.service.resource.type.AgreementService;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This service offers method for updating entities.
 */
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
    private final @NonNull AgreementArtifactLinker agreementArtifactLinker;

    /**
     * Service for artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Helper for creating self links.
     */
    private final @NonNull SelfLinkHelper selfLinkHelper;

    /**
     * Update database resource.
     *
     * @param resource The ids resource.
     */
    public void updateResource(final Resource resource) {
        try {
            final var updated = requestedResourceUpdater.update(resource);
            if (log.isDebugEnabled()) {
                log.debug("Updated resource. [id=({})]", selfLinkHelper.getSelfLink(updated));
            }

            final var representations = resource.getRepresentation();
            for (final var rep : Utils.requireNonNull(representations, ErrorMessage.LIST_NULL)) {
                updateRepresentation(rep);
            }
        } catch (ResourceNotFoundException | IllegalArgumentException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to update resource. [id=({})]", resource.getId());
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
                log.debug("Updated representation. [id=({})]",
                        selfLinkHelper.getSelfLink(updated));
            }

            final var artifacts = representation.getInstance();
            for (final var artifact : Utils.requireNonNull(artifacts, ErrorMessage.LIST_NULL)) {
                updateArtifact((Artifact) artifact);
            }
        } catch (ResourceNotFoundException | IllegalArgumentException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to update representation. [id=({})]", representation.getId());
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
            if (log.isDebugEnabled()) {
                log.debug("Updated artifact. [id=({})]", selfLinkHelper.getSelfLink(updated));
            }
        } catch (ResourceNotFoundException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to update artifact. [id=({})]", artifact.getId());
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
                log.debug("Failed to confirm agreement. [id=({})]", agreement.getId());
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
