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
package io.dataspaceconnector.controller.resource.view.app;

import java.util.UUID;

import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.relation.AppsToEndpointsController;
import io.dataspaceconnector.controller.resource.type.AppController;
import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import io.dataspaceconnector.controller.resource.view.util.SelfLinking;
import io.dataspaceconnector.model.app.App;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles the REST resource for an app.
 */
@Component
public class AppViewAssembler extends SelfLinkHelper
        implements RepresentationModelAssembler<App, AppView>, SelfLinking {

    @Override
    public final AppView toModel(final App app) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(app, AppView.class);
        view.add(getSelfLink(app.getId()));

        final var endpointLink = linkTo(methodOn(AppsToEndpointsController.class)
                .getResource(app.getId(), null, null))
                .withRel(BaseType.ENDPOINTS);
        view.add(endpointLink);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return getSelfLink(entityId, AppController.class);
    }

}
