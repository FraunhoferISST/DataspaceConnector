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

import de.fraunhofer.iais.eis.RouteStep;
import de.fraunhofer.iais.eis.RouteStepBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.common.net.ApiReferenceHelper;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.model.route.Route;
import org.springframework.stereotype.Component;

/**
 * Converts DSC routes to IDS route steps.
 */
@Component
public final class IdsRouteStepBuilder extends IdsRouteBuilder<RouteStep> {

    /**
     * Constructs an IdsRouteStepBuilder.
     *
     * @param selfLinkHelper     The self link helper.
     * @param idsEndpointBuilder The endpoint builder.
     * @param idsArtifactBuilder The artifact builder.
     * @param apiReferenceHelper The API reference helper.
     */
    public IdsRouteStepBuilder(final SelfLinkHelper selfLinkHelper,
                               final IdsEndpointBuilder idsEndpointBuilder,
                               final IdsArtifactBuilder idsArtifactBuilder,
                               final ApiReferenceHelper apiReferenceHelper) {
        super(selfLinkHelper, idsEndpointBuilder, idsArtifactBuilder, apiReferenceHelper);
    }

    @Override
    protected RouteStep createInternal(final Route route, final int currentDepth,
                                       final int maxDepth) throws ConstraintViolationException {

        final var start = buildRouteStart(route, currentDepth, maxDepth);
        final var end = buildRouteEnd(route, currentDepth, maxDepth);

        final var builder = new RouteStepBuilder(getAbsoluteSelfLink(route))
                ._routeDescription_(route.getDescription())
                ._routeDeployMethod_(route.getDeploy().toString())
                ._routeConfiguration_(route.getConfiguration());
        start.ifPresent(builder::_appRouteStart_);
        end.ifPresent(builder::_appRouteEnd_);
        return builder.build();
    }

}
