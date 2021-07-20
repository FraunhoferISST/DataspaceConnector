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
package io.dataspaceconnector.view.endpoint;

import io.dataspaceconnector.controller.configuration.EndpointController;
import io.dataspaceconnector.controller.resource.view.SelfLinking;
import io.dataspaceconnector.controller.resource.view.ViewAssemblerHelper;
import io.dataspaceconnector.model.endpoint.ConnectorEndpoint;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Assembles the REST resource for an connector endpoint.
 */
@Component
public class ConnectorEndpointViewAssembler implements
        RepresentationModelAssembler<ConnectorEndpoint, ConnectorEndpointView>, SelfLinking {

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, EndpointController.class);
    }

    @Override
    public final ConnectorEndpointView toModel(final ConnectorEndpoint connectorEndpoint) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(connectorEndpoint,
                ConnectorEndpointView.class);
        view.add(getSelfLink(connectorEndpoint.getId()));

        return view;
    }
}
