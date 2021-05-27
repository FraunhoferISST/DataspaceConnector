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
package io.dataspaceconnector.view;

import io.dataspaceconnector.controller.configurations.ConfigmanagerController.ConnectorController;
import io.dataspaceconnector.model.Connector;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Assembles the REST resource for a connector.
 */
@Component
public class ConnectorViewAssembler implements
        RepresentationModelAssembler<Connector, ConnectorView>, SelfLinking {


    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, ConnectorController.class);
    }

    @Override
    public final ConnectorView toModel(final Connector connector) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(connector, ConnectorView.class);
        view.add(getSelfLink(connector.getId()));

        return view;
    }
}
