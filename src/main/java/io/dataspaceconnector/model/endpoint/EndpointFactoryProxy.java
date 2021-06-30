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
package io.dataspaceconnector.model.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EndpointFactoryProxy extends EndpointFactory<Endpoint, EndpointDesc> {

    @Autowired
    private AppEndpointFactory apps;

    @Autowired
    private ConnectorEndpointFactory connector;

    @Autowired
    private GenericEndpointFactory generic;

    @Override
    protected Endpoint initializeEntity(final EndpointDesc desc) {
        if (AppEndpoint.class.equals(desc.getClass())) {
            return apps.initializeEntity((AppEndpointDesc) desc);
        } else if (ConnectorEndpoint.class.equals(desc.getClass())) {
            return connector.initializeEntity((ConnectorEndpointDesc) desc);
        }

        return generic.initializeEntity((GenericEndpointDesc) desc);
    }

    @Override
    protected boolean updateInternal(final Endpoint endpoint, final EndpointDesc desc) {
        if (AppEndpoint.class.equals(desc.getClass())) {
            return apps.updateInternal((AppEndpoint) endpoint, (AppEndpointDesc) desc);
        } else if (ConnectorEndpoint.class.equals(desc.getClass())) {
            return connector.updateInternal((ConnectorEndpoint) endpoint, (ConnectorEndpointDesc) desc);
        }

        return generic.updateInternal((GenericEndpoint) endpoint, (GenericEndpointDesc) desc);
    }
}
