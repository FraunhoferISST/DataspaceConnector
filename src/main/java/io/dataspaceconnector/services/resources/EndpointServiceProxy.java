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
package io.dataspaceconnector.services.resources;

import java.util.UUID;

import io.dataspaceconnector.exceptions.ResourceNotFoundException;
import io.dataspaceconnector.model.endpoint.AppEndpoint;
import io.dataspaceconnector.model.endpoint.AppEndpointDesc;
import io.dataspaceconnector.model.endpoint.ConnectorEndpoint;
import io.dataspaceconnector.model.endpoint.ConnectorEndpointDesc;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.endpoint.EndpointDesc;
import io.dataspaceconnector.repositories.EndpointRepository;
import io.dataspaceconnector.services.configuration.AppEndpointService;
import io.dataspaceconnector.services.configuration.ConnectorEndpointService;
import io.dataspaceconnector.services.configuration.GenericEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EndpointServiceProxy implements EntityService<Endpoint, EndpointDesc> {

    @Autowired
    GenericEndpointService generic;

    @Autowired
    AppEndpointService apps;

    @Autowired
    ConnectorEndpointService connector;

    @Autowired
    private EndpointRepository repository;

    private <X extends Endpoint, Y extends EndpointDesc> EntityService<X, Y> getService(final Class<?> clazz) {
        if (AppEndpointDesc.class.equals(clazz) || AppEndpoint.class.equals(clazz)) {
            return (EntityService<X, Y>) apps;
        } else if (ConnectorEndpointDesc.class.equals(clazz) || ConnectorEndpoint.class.equals(clazz)) {
            return (EntityService<X, Y>) connector;
        }

        return (EntityService<X, Y>) generic;
    }

    @Override
    public Endpoint create(final EndpointDesc desc) {
        return getService(desc.getClass()).create(desc);
    }

    @Override
    public Endpoint update(final UUID entityId, final EndpointDesc desc) {
        return getService(desc.getClass()).update(entityId, desc);
    }

    @Override
    public Endpoint get(final UUID entityId) {
        try {
            return apps.get(entityId);
        } catch(ResourceNotFoundException ignored) { }

        try {
            return connector.get(entityId);
        } catch(ResourceNotFoundException ignored) { }

        return generic.get(entityId);
    }

    @Override
    public Page<Endpoint> getAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public boolean doesExist(final UUID entityId) {
        try {
            return apps.doesExist(entityId);
        } catch(ResourceNotFoundException ignored) { }

        try {
            return connector.doesExist(entityId);
        } catch(ResourceNotFoundException ignored) { }

        return generic.doesExist(entityId);
    }

    @Override
    public void delete(final UUID entityId) {
        var endpoint = get(entityId);
        var service = getService(endpoint.getClass());
        service.delete(entityId);
    }
}
