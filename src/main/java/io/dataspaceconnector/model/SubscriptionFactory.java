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

import io.dataspaceconnector.exception.InvalidEntityException;
import io.dataspaceconnector.util.ErrorMessages;
import io.dataspaceconnector.util.MetadataUtils;
import io.dataspaceconnector.util.Utils;
import io.dataspaceconnector.util.ValidationUtils;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Creates and updates a subscription.
 */
@Component
public class SubscriptionFactory implements AbstractFactory<Subscription, SubscriptionDesc> {

    /**
     * Creates a new subscription.
     *
     * @param desc The description of the new subscription.
     * @return The new subscription.
     * @throws IllegalArgumentException if desc is null.
     * @throws InvalidEntityException   if no valid entity can be created from the description.
     */
    @Override
    public Subscription create(final SubscriptionDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var subscription = new Subscription();
        subscription.setUrl(desc.getUrl());
        subscription.setTarget(desc.getTarget());
        subscription.setPushData(desc.isPushData());
        subscription.setSubscriber(desc.getSubscriber());

        update(subscription, desc);

        return subscription;
    }

    /**
     * Updates a subscription.
     *
     * @param subscription The subscription to be updated.
     * @param desc         The new subscription description.
     * @return true, if the subscription has been modified; false otherwise.
     * @throws IllegalArgumentException if any of the parameters is null.
     * @throws InvalidEntityException   if no valid entity can be created from the description.
     */
    @Override
    public boolean update(final Subscription subscription, final SubscriptionDesc desc) {
        Utils.requireNonNull(subscription, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedUrl = this.updateUrl(subscription, desc.getUrl());
        final var hasUpdatedTarget = this.updateTarget(subscription, desc.getTarget());
        final var hasUpdatedPushData = this.updatePushData(subscription, desc.isPushData());
        final var hasUpdatedSubscriber = this.updateSubscriber(subscription, desc.getSubscriber());

        return hasUpdatedUrl || hasUpdatedTarget || hasUpdatedPushData || hasUpdatedSubscriber;
    }

    /**
     * Updates the URL of a subscription.
     *
     * @param subscription The subscription.
     * @param url          The new URL.
     * @return true, if the URL was updated; false otherwise.
     */
    private boolean updateUrl(final Subscription subscription, final URI url) {
        if (url == null || ValidationUtils.isInvalidUri(String.valueOf(url))) {
            throw new InvalidEntityException(ErrorMessages.INVALID_ENTITY_INPUT.toString());
        }

        final var newUri = MetadataUtils.updateUri(subscription.getUrl(), url, null);
        newUri.ifPresent(subscription::setUrl);

        return newUri.isPresent();
    }

    /**
     * Updates the target of a subscription.
     *
     * @param subscription The subscription.
     * @param target       The new target.
     * @return true, if the URL was updated; false otherwise.
     */
    private boolean updateTarget(final Subscription subscription, final URI target) {
        if (target == null || ValidationUtils.isInvalidUri(String.valueOf(target))) {
            throw new InvalidEntityException(ErrorMessages.INVALID_ENTITY_INPUT.toString());
        }

        final var newTarget = MetadataUtils.updateUri(subscription.getTarget(), target, null);
        newTarget.ifPresent(subscription::setTarget);

        return newTarget.isPresent();
    }

    /**
     * Updates the subscriber of a subscription.
     *
     * @param subscription The subscription.
     * @param subscriber   The new subscriber.
     * @return true, if the URL was updated; false otherwise.
     */
    private boolean updateSubscriber(final Subscription subscription, final URI subscriber) {
        if (subscriber == null || ValidationUtils.isInvalidUri(String.valueOf(subscriber))) {
            throw new InvalidEntityException(ErrorMessages.INVALID_ENTITY_INPUT.toString());
        }

        final var newSubscriber =
                MetadataUtils.updateUri(subscription.getSubscriber(), subscriber, null);
        newSubscriber.ifPresent(subscription::setSubscriber);

        return newSubscriber.isPresent();
    }

    /**
     * Updates the push value of a subscription.
     *
     * @param subscription The subscription.
     * @param push         The new push value.
     * @return true, if the URL was updated; false otherwise.
     */
    private boolean updatePushData(final Subscription subscription, final boolean push) {
        final var newPushValue =
                MetadataUtils.updateBoolean(subscription.isPushData(), push, false);
        newPushValue.ifPresent(subscription::setPushData);

        return newPushValue.isPresent();
    }

}
