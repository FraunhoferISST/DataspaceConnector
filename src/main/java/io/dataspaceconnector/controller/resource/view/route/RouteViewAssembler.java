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
package io.dataspaceconnector.controller.resource.view.route;

import java.util.UUID;

import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.relation.RoutesToStepsController;
import io.dataspaceconnector.controller.resource.type.RouteController;
import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import io.dataspaceconnector.controller.resource.view.util.SelfLinking;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import io.dataspaceconnector.model.route.Route;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles the REST resource for a route.
 */
@Component
public class RouteViewAssembler extends SelfLinkHelper
        implements RepresentationModelAssembler<Route, RouteView>, SelfLinking {

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return getSelfLink(entityId, RouteController.class);
    }

    @Override
    public final RouteView toModel(final Route route) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(route,
                RouteView.class);
        setEndpointType(view);
        view.add(getSelfLink(route.getId()));

        final var steps = linkTo(methodOn(RoutesToStepsController.class)
                .getResource(route.getId(), null, null))
                .withRel(BaseType.ROUTES);
        view.add(steps);

        final var output = linkTo(methodOn(RouteController.class)
                .getOutput(route.getId())).withRel("output");
        view.add(output);

        return view;
    }

    /**
     * This method determines the endpoint type.
     *
     * @param view The route view.
     */
    private void setEndpointType(final RouteView view) {
        if (view != null) {
            if (view.getStart() != null) {
                if (view.getStart() instanceof GenericEndpoint) {
                    final var end = (GenericEndpoint) view.getStart();
                    end.setType("GENERIC");
                }
            }
            if (view.getEnd() != null) {
                if (view.getEnd() instanceof GenericEndpoint) {
                    final var end = (GenericEndpoint) view.getEnd();
                    end.setType("GENERIC");
                }
            }
        }
    }
}
