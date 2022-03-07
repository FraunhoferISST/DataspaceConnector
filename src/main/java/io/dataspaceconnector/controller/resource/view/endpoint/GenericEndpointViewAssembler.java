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

import java.util.UUID;

import io.dataspaceconnector.controller.resource.type.EndpointController;
import io.dataspaceconnector.controller.resource.view.datasource.DataSourceViewAssembler;
import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import io.dataspaceconnector.controller.resource.view.util.SelfLinking;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * Assembles the REST resource for a generic endpoint.
 */
@Component
public class GenericEndpointViewAssembler extends SelfLinkHelper
        implements RepresentationModelAssembler<GenericEndpoint, GenericEndpointView>, SelfLinking {

    /**
     * Assembler for getting the url of the endpoint's datasource.
     */
    @Autowired
    private DataSourceViewAssembler dataSourceViewAssembler;

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return getSelfLink(entityId, EndpointController.class);
    }

    @Override
    public final GenericEndpointView toModel(final GenericEndpoint endpoint) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(endpoint, GenericEndpointView.class);
        view.add(getSelfLink(endpoint.getId()));

        if (endpoint.getDataSource() != null) {
            view.add(dataSourceViewAssembler.getSelfLink(endpoint.getDataSource().getId())
                    .withRel("datasource"));
        }

        return view;
    }
}
