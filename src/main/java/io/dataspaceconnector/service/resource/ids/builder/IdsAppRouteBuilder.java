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
package io.dataspaceconnector.service.resource.ids.builder;

import de.fraunhofer.iais.eis.AppRoute;
import de.fraunhofer.iais.eis.AppRouteBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.service.resource.ids.builder.base.AbstractIdsBuilder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converts dsc routes to ids app routes.
 */
@Component
public final class IdsAppRouteBuilder extends AbstractIdsBuilder<Route, AppRoute> {

    /**
     * The builder for IDS endpoints.
     */
    private final @NonNull IdsEndpointBuilder endpointBuilder;

    /**
     * The builder for IDS route steps.
     */
    private final @NonNull IdsRouteStepBuilder routeStepBuilder;

    /**
     * Constructs an IdsAppRouteBuilder.
     *
     * @param selfLinkHelper the self link helper.
     * @param idsEndpointBuilder the endpoint builder.
     * @param idsRouteStepBuilder the route step builder.
     */
    @Autowired
    public IdsAppRouteBuilder(final SelfLinkHelper selfLinkHelper,
                              final IdsEndpointBuilder idsEndpointBuilder,
                              final IdsRouteStepBuilder idsRouteStepBuilder) {
        super(selfLinkHelper);
        this.endpointBuilder = idsEndpointBuilder;
        this.routeStepBuilder = idsRouteStepBuilder;
    }

    @Override
    protected AppRoute createInternal(final Route route, final int currentDepth,
                                      final int maxDepth) throws ConstraintViolationException {

        final var start = create(endpointBuilder,
                Util.asList(route.getStart()), currentDepth, maxDepth);

        final var end = create(endpointBuilder,
                Util.asList(route.getEnd()), currentDepth, maxDepth);

        final var routeSteps = create(routeStepBuilder, route.getSteps(),
                currentDepth, maxDepth);

        final var deployMode = route.getDeploy().toString();
        final var configuration = route.getConfiguration();
        final var description = route.getDescription();

        final var builder = new AppRouteBuilder(getAbsoluteSelfLink(route))
                ._routeDescription_(description)
                ._routeDeployMethod_(deployMode)
                ._routeConfiguration_(configuration);
        start.ifPresent(builder::_appRouteStart_);
        end.ifPresent(builder::_appRouteEnd_);
        routeSteps.ifPresent(builder::_hasSubRoute_);
        return builder.build();
    }

}
