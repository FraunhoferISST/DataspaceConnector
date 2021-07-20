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
package io.dataspaceconnector.service.resource;

import java.util.UUID;

import io.dataspaceconnector.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.endpoint.ConnectorEndpoint;
import io.dataspaceconnector.model.endpoint.ConnectorEndpointDesc;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.endpoint.EndpointDesc;
import io.dataspaceconnector.repository.EndpointRepository;
import io.dataspaceconnector.service.configuration.ConnectorEndpointService;
import io.dataspaceconnector.service.configuration.GenericEndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for endpoint proxy.
 */
@Service
public class EndpointServiceProxy implements EntityService<Endpoint, EndpointDesc> {

    /**
     * The generic endpoint service.
     */
    @Autowired
    private GenericEndpointService generic;

    /**
     * The connector endpoint service.
     */
    @Autowired
    private ConnectorEndpointService connector;

    /**
     * The endpoint repository.
     */
    @Autowired
    private EndpointRepository repository;

    /**
     * @param clazz The class.
     * @param <X> Types of endpoint.
     * @param <Y> Types of the endpoint description.
     * @return entity service.
     */
    @SuppressWarnings("unchecked")
    private <X extends Endpoint, Y extends EndpointDesc> EntityService<X, Y>
    getService(final Class<?> clazz) {
        if (ConnectorEndpointDesc.class.equals(clazz)
                || ConnectorEndpoint.class.equals(clazz)) {
            return (EntityService<X, Y>) connector;
        }

        return (EntityService<X, Y>) generic;
    }

    @Override
    public final Endpoint create(final EndpointDesc desc) {
        return getService(desc.getClass()).create(desc);
    }

    @Override
    public final Endpoint update(final UUID entityId, final EndpointDesc desc) {
        return getService(desc.getClass()).update(entityId, desc);
    }

    @Override
    public final Endpoint get(final UUID entityId) {

        try {
            return connector.get(entityId);
        } catch (ResourceNotFoundException ignored) { }

        return generic.get(entityId);
    }

    @Override
    public final Page<Endpoint> getAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public final boolean doesExist(final UUID entityId) {
        return   connector.doesExist(entityId)
                || generic.doesExist(entityId);
    }

    @Override
    public final void delete(final UUID entityId) {
        var endpoint = get(entityId);
        var service = getService(endpoint.getClass());
        service.delete(entityId);
    }
}
