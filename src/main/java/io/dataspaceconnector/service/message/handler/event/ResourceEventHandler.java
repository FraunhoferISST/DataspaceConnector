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
package io.dataspaceconnector.service.message.handler.event;

import de.fraunhofer.iais.eis.Resource;
import io.dataspaceconnector.service.message.SubscriberNotificationService;
import io.dataspaceconnector.service.resource.type.RequestedResourceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Handles resource update events by notifying listening subscribers.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class ResourceEventHandler {

    /**
     * Service for requested resources.
     */
    private final @NonNull RequestedResourceService requestedResourceSvc;

    /**
     * Service for notifying subscribers about an entity update.
     */
    private final @NonNull SubscriberNotificationService subscriberNotificationSvc;

    /**
     * Notifies subscribers on updated resource.
     *
     * @param resource The updated ids resource.
     */
    @Async
    @EventListener
    public void handleResourceUpdateEvent(final Resource resource) {
        final var requestedResource = requestedResourceSvc.getEntityByRemoteId(resource.getId());
        try {
            requestedResource.ifPresent(subscriberNotificationSvc::notifyOnUpdate);
        } catch (Exception exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to notify subscribers for update to resource. [remoteId=({})]",
                        resource.getId());
            }
        }
    }

}
