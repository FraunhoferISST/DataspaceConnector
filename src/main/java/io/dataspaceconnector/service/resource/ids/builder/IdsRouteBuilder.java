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
package io.dataspaceconnector.service.resource.ids.builder;

import java.util.List;
import java.util.Optional;

import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.Endpoint;
import de.fraunhofer.iais.eis.ModelClass;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.common.net.ApiReferenceHelper;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.service.resource.ids.builder.base.AbstractIdsBuilder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class for IDS builders that construct IDS objects from DSC routes.
 *
 * @param <X> The type of IDS object created from the DSC route.
 */
public abstract class IdsRouteBuilder<X extends ModelClass> extends AbstractIdsBuilder<Route, X> {

    /**
     * The builder for IDS endpoints.
     */
    private final @NonNull IdsEndpointBuilder endpointBuilder;

    /**
     * The builder for ids artifacts.
     */
    private final @NonNull IdsArtifactBuilder artifactBuilder;

    /**
     * Helper class for managing API endpoint references.
     */
    private final @NonNull ApiReferenceHelper apiReferenceHelper;

    /**
     * Constructs an IdsRouteBuilder.
     *
     * @param selfLinkHelper the self link helper.
     * @param idsEndpointBuilder the endpoint builder.
     * @param idsArtifactBuilder the artifact builder.
     * @param referenceHelper the API reference helper.
     */
    @Autowired
    protected IdsRouteBuilder(final SelfLinkHelper selfLinkHelper,
                           final @NonNull IdsEndpointBuilder idsEndpointBuilder,
                           final @NonNull IdsArtifactBuilder idsArtifactBuilder,
                           final @NonNull ApiReferenceHelper referenceHelper) {
        super(selfLinkHelper);
        this.endpointBuilder = idsEndpointBuilder;
        this.artifactBuilder = idsArtifactBuilder;
        this.apiReferenceHelper = referenceHelper;
    }

    protected abstract X createInternal(Route entity, int currentDepth, int maxDepth)
            throws ConstraintViolationException;

    /**
     * Builds the start for a route. The start may be null if the route is used to dispatch data
     * from the DSC to backends.
     *
     * @param route        The route.
     * @param currentDepth Current depth of the build process.
     * @param maxDepth     Maximum depth of the build process.
     * @return An optional containing the built endpoint, if any.
     */
    protected Optional<List<Endpoint>> buildRouteStart(final Route route, final int currentDepth,
                                                       final int maxDepth) {
        Optional<List<Endpoint>> start = Optional.empty();
        if (route.getStart() != null) {
            start = create(endpointBuilder, Util.asList(route.getStart()), currentDepth, maxDepth);
        }
        return start;
    }

    /**
     * Builds the end for a route. If the route end is null, but the route references an artifact
     * as output, an IDS {@link de.fraunhofer.iais.eis.ConnectorEndpoint} is built from the
     * artifact.
     *
     * @param route        The route.
     * @param currentDepth Current depth of the build process.
     * @param maxDepth     Maximum depth of the build process.
     * @return An optional containing the built endpoint, if any.
     */
    protected Optional<List<Endpoint>> buildRouteEnd(final Route route, final int currentDepth,
                                                     final int maxDepth) {
        Optional<List<Endpoint>> end = Optional.empty();
        if (route.getEnd() != null) {
            end = create(endpointBuilder, Util.asList(route.getEnd()), currentDepth, maxDepth);
        } else {
            final var output = route.getOutput();
            if (output != null) {
                final var artifacts =
                        create(artifactBuilder, Util.asList(output), currentDepth, maxDepth);
                final var accessUrl = apiReferenceHelper.getDataUri(output);

                final var builder = new ConnectorEndpointBuilder(accessUrl)
                        ._endpointInformation_(new TypedLiteral("Connector endpoint for artifact "
                                + output.getId(), "en"))
                        ._accessURL_(accessUrl);
                artifacts.ifPresent(a -> builder._endpointArtifact_(artifacts.get().get(0)));

                end = Optional.of(Util.asList(builder.build()));
            }
        }
        return end;
    }

}
