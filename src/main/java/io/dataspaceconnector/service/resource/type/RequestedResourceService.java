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

import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.resource.RequestedResourceDesc;
import io.dataspaceconnector.repository.BaseEntityRepository;
import io.dataspaceconnector.repository.RequestedResourcesRepository;
import io.dataspaceconnector.service.resource.base.RemoteResolver;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles the basic logic for requested resources.
 */
public class RequestedResourceService extends ResourceService<RequestedResource,
        RequestedResourceDesc> implements RemoteResolver {

    /**
     * Constructor.
     *
     * @param repository The requested resource repository.
     * @param factory    The requested resource factory.
     */
    public RequestedResourceService(final BaseEntityRepository<RequestedResource> repository,
                                    final AbstractFactory<RequestedResource,
                                            RequestedResourceDesc> factory) {
        super(repository, factory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UUID> identifyByRemoteId(final URI remoteId) {
        return ((RequestedResourcesRepository) getRepository()).identifyByRemoteId(remoteId);
    }

    /**
     * Find requested resource by remote id.
     *
     * @param remoteId The remote id.
     * @return The entity.
     */
    public Optional<RequestedResource> getEntityByRemoteId(final URI remoteId) {
        final var repo = (RequestedResourcesRepository) getRepository();
        return repo.getByRemoteId(remoteId);
    }
}
