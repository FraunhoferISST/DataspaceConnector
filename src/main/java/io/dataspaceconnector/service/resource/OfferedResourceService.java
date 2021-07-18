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

import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.OfferedResourceDesc;
import io.dataspaceconnector.service.message.subscription.SubscriberNotificationService;
import io.dataspaceconnector.util.ErrorMessages;
import io.dataspaceconnector.util.Utils;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Handles the basic logic for offered resources.
 */
@Service
public class OfferedResourceService extends ResourceService<OfferedResource, OfferedResourceDesc> {

    /**
     * Service for notifying subscribers about an entity update.
     */
    private final @NonNull SubscriberNotificationService subscriberNotificationSvc;

    /**
     * Constructor for RequestedResourceService.
     *
     * @param subscriberSvc Service for notifying subscribers about an entity update.
     */
    @Autowired
    public OfferedResourceService(final @NonNull SubscriberNotificationService subscriberSvc) {
        super();
        this.subscriberNotificationSvc = subscriberSvc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OfferedResource update(final UUID entityId, final OfferedResourceDesc desc) {
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
}
