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
package io.dataspaceconnector.model;

import io.dataspaceconnector.exceptions.InvalidEntityException;
import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Creates and updates a subscriber.
 */
@Component
public class SubscriptionFactory implements AbstractFactory<Subscription, SubscriptionDesc> {

    /**
     * Creates a new subscription.
     *
     * @param desc The description of the new subscription.
     * @return the new subscription.
     * @throws IllegalArgumentException if desc is null.
     * @throws InvalidEntityException if no valid entity can be created from the description.
     */
    @Override
    public Subscription create(final SubscriptionDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var subscription = new Subscription();
        subscription.setSubscriber(desc.getSubscriber());
        subscription.setUrl(desc.getUrl());
        subscription.setTarget(desc.getTarget());

        update(subscription, desc);

        return subscription;
    }

    /**
     * Updates a subscription.
     *
     * @param subscription the subscription to be updated.
     * @param desc The new subscription description.
     * @return true, if the subscription has been modified; false otherwise.
     * @throws IllegalArgumentException if any of the parameters is null.
     * @throws InvalidEntityException if no valid entity can be created from the description.
     */
    @Override
    public boolean update(final Subscription subscription, final SubscriptionDesc desc) {
        Utils.requireNonNull(subscription, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        return updateUrl(subscription, desc.getUrl());
    }

    /**
     * Updates the URL for a subscriber.
     * @param subscription the subscriber.
     * @param url the new URL.
     * @return true, if the URL was updated; false otherwise.
     */
    private boolean updateUrl(final Subscription subscription, final URI url) {
        if (url == null) {
            throw new InvalidEntityException(ErrorMessages.INVALID_ENTITY_INPUT.toString());
        }

        final var newUri = MetadataUtils.updateUri(subscription.getUrl(), url, null);
        newUri.ifPresent(subscription::setUrl);

        return newUri.isPresent();
    }

}
