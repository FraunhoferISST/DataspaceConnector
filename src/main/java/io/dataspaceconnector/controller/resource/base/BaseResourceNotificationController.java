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
package io.dataspaceconnector.controller.resource.base;

import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.dataspaceconnector.model.base.Description;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.service.message.SubscriberNotificationService;
import io.dataspaceconnector.service.resource.base.EntityService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * Offers methods for resource handling. Notifies subscribers on update event.
 *
 * @param <T> The type of the resource.
 * @param <D> The type of the resource description expected to be passed with REST calls.
 * @param <V> The type of the view produces by this controller.
 * @param <S> The underlying service for handling the resource logic.
 */
@ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
        description = ResponseDescription.UNAUTHORIZED)
public class BaseResourceNotificationController<T extends Entity, D extends Description, V
        extends RepresentationModel<V>, S extends EntityService<T, D>>
        extends BaseResourceController<T, D, V, S> {

    /**
     * Service for notifying subscribers about an entity update.
     */
    @Autowired
    private SubscriberNotificationService subscriberNotificationSvc;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<V> update(final UUID resourceId, final D desc) {
        final var tmp = super.update(resourceId, desc);

        // Notify subscribers on update event.
        subscriberNotificationSvc.notifyOnUpdate(getService().get(resourceId));

        return tmp;
    }
}
