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

import io.dataspaceconnector.model.Subscription;
import io.dataspaceconnector.model.SubscriptionDesc;
import io.dataspaceconnector.repository.SubscriptionRepository;
import io.dataspaceconnector.util.ErrorMessages;
import io.dataspaceconnector.util.Utils;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

/**
 * Handles the basic logic for subscriptions.
 */
@Service
@NoArgsConstructor
public class SubscriptionService extends BaseEntityService<Subscription, SubscriptionDesc> {

    /**
     * Get a list of all subscriptions with a matching subscriber.
     *
     * @param pageable   Range selection of the complete data set.
     * @param subscriber The subscriber id.
     * @return The id list of all entities.
     * @throws IllegalArgumentException if the passed pageable is null.
     */
    public List<Subscription> getOwnSubscriptions(final Pageable pageable, final URI subscriber) {
        Utils.requireNonNull(pageable, ErrorMessages.PAGEABLE_NULL);
        return ((SubscriptionRepository) getRepository()).findAllBySubscriber(subscriber);
    }
}
