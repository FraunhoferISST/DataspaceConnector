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
package io.dataspaceconnector.controller.resource.view.endpoint;

import io.dataspaceconnector.model.endpoint.AppEndpoint;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * Assembler for the Endpoint-View-Proxy.
 */
@Component
public class EndpointViewAssemblerProxy
        implements RepresentationModelAssembler<Endpoint, RepresentationModel<?>> {

    /**
     * Assembler for generic endpoints.
     */
    @Autowired
    private GenericEndpointViewAssembler genericAssembler;

    /**
     * Assembler for app endpoints.
     */
    @Autowired
    private AppEndpointViewAssembler appEndpointViewAssembler;

    /**
     * Converts an endpoint object into an app-/connector- or generic-endpoint.
     *
     * @param endpoint The endpoint.
     * @return The endpoint of a specific type.
     */
    @Override
    public RepresentationModel<?> toModel(final Endpoint endpoint) {
        if (endpoint instanceof AppEndpoint) {
            return appEndpointViewAssembler.toModel((AppEndpoint) endpoint);
        }

        assert endpoint instanceof GenericEndpoint;
        return genericAssembler.toModel((GenericEndpoint) endpoint);
    }
}
