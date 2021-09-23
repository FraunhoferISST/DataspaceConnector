/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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

package io.dataspaceconnector.service.resource.relation;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import io.dataspaceconnector.common.exception.InvalidEntityException;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.util.ApiReferenceHelper;
import io.dataspaceconnector.common.util.UUIDUtils;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.artifact.RemoteData;
import io.dataspaceconnector.model.configuration.DeployMethod;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.service.resource.type.RouteService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Manages the relation between artifacts and routes.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class ArtifactRouteService {

    /**
     * Service for managing routes.
     */
    private final @NonNull RouteService routeSvc;

    /**
     * Helper class for managing API endpoint references.
     */
    private final @NonNull ApiReferenceHelper apiReferenceHelper;

    /**
     * Checks whether the route referenced by a URL can be linked to an artifact. If the route
     * is already linked to a different artifact, the new link cannot be created.
     *
     * @param url        The URL.
     * @param artifactId The artifact ID.
     * @throws InvalidEntityException if the URL is not a valid URI, the route is already linked to
     *                                another artifact or the route cannot be found.
     */
    public void ensureSingleArtifactPerRoute(final URL url, final UUID artifactId) {
        try {
            if (apiReferenceHelper.isRouteReference(url)) {
                final var routeId = UUIDUtils.uuidFromUri(url.toURI());
                final var route = routeSvc.get(routeId);

                if (route.getOutput() != null
                        && !route.getOutput().getId().equals(artifactId)) {
                    throw new InvalidEntityException("Referenced route is already linked to "
                            + "an artifact.");
                }
            }
        } catch (URISyntaxException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Route ID in access URL of artifact is not a valid URI. "
                                + "[accessUrl=({}), exception=({})]", url, exception.getMessage(),
                        exception);
            }
            throw new InvalidEntityException("Route ID in access URL of artifact is not a "
                    + "valid URI.");
        } catch (ResourceNotFoundException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Could not find route matching the access URL. "
                        + "[accessUrl=({})]", url, exception);
            }
            throw new InvalidEntityException("Could not find route matching the access URL.");
        }
    }

    /**
     * Checks whether an artifact has to be removed from a route on update. If the artifact's
     * access URL referenced a route before and does not reference the same route after the update,
     * the link between artifact and route is removed.
     *
     * @param artifact The updated artifact.
     * @param url      The new access URL.
     */
    public void checkForRouteLinkUpdate(final Artifact artifact, final URL url) {
        if (artifact.getId() != null) {
            final var route = routeSvc.getByOutput(artifact);
            if (route != null) {
                final var urlString = url.toString();
                final boolean isRouteReference = apiReferenceHelper.isRouteReference(url);
                if (!isRouteReference || !urlString.contains(route.getId().toString())) {
                    routeSvc.removeOutput(route.getId());
                }
            }
        }
    }

    /**
     * Links an artifact to a route.
     *
     * @param url        The URL referencing a route.
     * @param artifactId The artifact ID.
     * @throws InvalidEntityException if the URL is not a valid URI or the referenced route does
     *                                not have deploy mode CAMEL.
     */
    public void createRouteLink(final URL url, final UUID artifactId) {
        try {
            if (apiReferenceHelper.isRouteReference(url)) {
                final var routeId = UUIDUtils.uuidFromUri(url.toURI());
                final var route = routeSvc.get(routeId);
                if (!DeployMethod.CAMEL.equals(route.getDeploy())) {
                    throw new InvalidEntityException("The referenced route does not have deploy"
                            + " mode CAMEL.");
                }
                routeSvc.setOutput(routeId, artifactId);
            }
        } catch (URISyntaxException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Route ID in access URL of artifact is not a valid URI. "
                                + "[accessUrl=({}), exception=({})]", url, exception.getMessage(),
                        exception);
            }
            throw new InvalidEntityException("Route ID in access URL of artifact is not a "
                    + "valid URI.");
        }
    }

    /**
     * Returns the route associated with an artifact, if any.
     *
     * @param artifact The artifact.
     * @return The associated route, if any. Null otherwise.
     * @throws ResourceNotFoundException if the referenced route is unknown.
     */
    public Route getAssociatedRoute(final ArtifactImpl artifact) {
        return routeSvc.getByOutput(artifact);
    }

    /**
     * Removes the link between an artifact and a route.
     *
     * @param artifact The artifact.
     */
    public void removeRouteLink(final ArtifactImpl artifact) {
        try {
            if (artifact.getData() instanceof RemoteData) {
                final var url = ((RemoteData) artifact.getData()).getAccessUrl();
                if (apiReferenceHelper.isRouteReference(url)) {
                    final var routeId = UUIDUtils.uuidFromUri(url.toURI());
                    routeSvc.removeOutput(routeId);
                }
            }
        } catch (URISyntaxException ignore) {
            // If the access URL is not a valid URI, route and artifact could not have been linked
        }
    }

}
