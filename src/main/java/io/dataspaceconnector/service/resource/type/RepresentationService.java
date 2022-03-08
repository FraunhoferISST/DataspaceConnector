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
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.representation.RepresentationDesc;
import io.dataspaceconnector.repository.BaseEntityRepository;
import io.dataspaceconnector.repository.RepresentationRepository;
import io.dataspaceconnector.service.resource.base.BaseEntityService;
import io.dataspaceconnector.service.resource.base.RemoteResolver;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing representations.
 */
public class RepresentationService extends BaseEntityService<Representation,
        RepresentationDesc> implements RemoteResolver {

    /**
     * Constructor.
     *
     * @param repository The representation repository.
     * @param factory    The representation factory.
     */
    public RepresentationService(
            final BaseEntityRepository<Representation> repository,
            final AbstractFactory<Representation, RepresentationDesc> factory) {
        super(repository, factory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UUID> identifyByRemoteId(final URI remoteId) {
        final var repo = (RepresentationRepository) getRepository();
        return repo.identifyByRemoteId(remoteId);
    }
}
