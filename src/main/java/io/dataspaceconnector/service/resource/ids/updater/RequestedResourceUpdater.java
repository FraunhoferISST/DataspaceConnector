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
package io.dataspaceconnector.service.resource.ids.updater;

import io.dataspaceconnector.common.ids.mapping.FromIdsObjectMapper;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.service.resource.type.RequestedResourceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Updates a dsc requested resource based on a provided ids resource.
 */
@Component
@RequiredArgsConstructor
public final class RequestedResourceUpdater
        implements InfomodelUpdater<de.fraunhofer.iais.eis.Resource, RequestedResource> {

    /**
     * Service for requested resources.
     */
    private final @NonNull RequestedResourceService requestedResourceService;

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestedResource update(final de.fraunhofer.iais.eis.Resource entity)
            throws ResourceNotFoundException {
        final var entityId = requestedResourceService.identifyByRemoteId(entity.getId());
        if (entityId.isEmpty()) {
            throw new ResourceNotFoundException(entity.getId().toString());
        }

        final var template = FromIdsObjectMapper.fromIdsResource(entity);
        return requestedResourceService.update(entityId.get(), template.getDesc());
    }
}
