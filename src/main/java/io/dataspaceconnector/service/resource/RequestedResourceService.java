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

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.resource.RequestedResourceDesc;
import io.dataspaceconnector.repository.RequestedResourcesRepository;
import org.springframework.stereotype.Service;

/**
 * Handles the basic logic for requested resources.
 */
@Service
public final class RequestedResourceService extends ResourceService<RequestedResource,
        RequestedResourceDesc> implements RemoteResolver {

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
