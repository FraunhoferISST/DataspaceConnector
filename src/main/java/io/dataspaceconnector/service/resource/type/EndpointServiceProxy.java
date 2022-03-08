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
package io.dataspaceconnector.service.resource.type;

import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.endpoint.AppEndpoint;
import io.dataspaceconnector.model.endpoint.AppEndpointDesc;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.endpoint.EndpointDesc;
import io.dataspaceconnector.repository.EndpointRepository;
import io.dataspaceconnector.service.resource.base.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service class for endpoint proxy.
 */
@Service
@Transactional
public class EndpointServiceProxy implements EntityService<Endpoint, EndpointDesc> {

    /**
     * The generic endpoint service.
     */
    @Autowired
    private GenericEndpointService generic;

    /**
     * The app endpoint service.
     */
    @Autowired
    private AppEndpointService app;

    /**
     * The endpoint repository.
     */
    @Autowired
    private EndpointRepository repository;

    /**
     * @param clazz The class.
     * @param <X>   Types of endpoint.
     * @param <Y>   Types of the endpoint description.
     * @return entity service.
     */
    @SuppressWarnings("unchecked")
    private <X extends Endpoint, Y extends EndpointDesc> EntityService<X, Y>
    getService(final Class<?> clazz) {
        if (AppEndpointDesc.class.equals(clazz) || AppEndpoint.class.equals(clazz)) {
            return (EntityService<X, Y>) app;
        }

        return (EntityService<X, Y>) generic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Endpoint create(final EndpointDesc desc) {
        return getService(desc.getClass()).create(desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Endpoint update(final UUID entityId, final EndpointDesc desc) {
        return getService(desc.getClass()).update(entityId, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Endpoint get(final UUID entityId) {
        try {
            return generic.get(entityId);
        } catch (ResourceNotFoundException ignored) { }
        return app.get(entityId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Endpoint> getAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean doesExist(final UUID entityId) {
        return generic.doesExist(entityId)
                || app.doesExist(entityId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final UUID entityId) {
        var endpoint = get(entityId);
        var service = getService(endpoint.getClass());
        service.delete(entityId);
    }
}
