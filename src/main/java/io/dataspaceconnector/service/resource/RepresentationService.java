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

import io.dataspaceconnector.model.Representation;
import io.dataspaceconnector.model.RepresentationDesc;
import io.dataspaceconnector.repository.RepresentationRepository;
import io.dataspaceconnector.service.message.subscription.SubscriberNotificationService;
import io.dataspaceconnector.util.ErrorMessages;
import io.dataspaceconnector.util.Utils;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing representations.
 */
@Service
public final class RepresentationService extends BaseEntityService<Representation,
        RepresentationDesc> implements RemoteResolver {

    /**
     * Service for notifying subscribers about an entity update.
     */
    private final @NonNull SubscriberNotificationService subscriberNotificationSvc;

    /**
     * Constructor for RepresentationService.
     *
     * @param subscriberSvc Service for notifying subscribers about an entity update.
     */
    @Autowired
    public RepresentationService(final @NonNull SubscriberNotificationService subscriberSvc) {
        super();
        this.subscriberNotificationSvc = subscriberSvc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Representation update(final UUID entityId, final RepresentationDesc desc) {
        Utils.requireNonNull(entityId, ErrorMessages.ENTITYID_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        var entity = get(entityId);

        if (getFactory().update(entity, desc)) {
            entity = persist(entity);
        }

        // Notify subscribers on update event.
        subscriberNotificationSvc.notifyOnUpdate(entity);

        return entity;
    }

    @Override
    public Optional<UUID> identifyByRemoteId(final URI remoteId) {
        final var repo = (RepresentationRepository) getRepository();
        return repo.identifyByRemoteId(remoteId);
    }
}
