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
package io.dataspaceconnector.controller.resource.view.endpoint;

import io.dataspaceconnector.controller.resource.type.EndpointController;
import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import io.dataspaceconnector.controller.resource.view.util.SelfLinking;
import io.dataspaceconnector.model.endpoint.AppEndpoint;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Assembles the REST resource for an app endpoint.
 */
@Component
public class AppEndpointViewAssembler extends SelfLinkHelper
        implements RepresentationModelAssembler<AppEndpoint, AppEndpointView>, SelfLinking {

    @Override
    public final AppEndpointView toModel(final AppEndpoint appEndpoint) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(appEndpoint, AppEndpointView.class);
        view.add(getSelfLink(appEndpoint.getId()));
        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return getSelfLink(entityId, EndpointController.class);
    }

}
