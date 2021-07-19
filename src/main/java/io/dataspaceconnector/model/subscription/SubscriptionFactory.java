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
package io.dataspaceconnector.model.subscription;

import java.net.URI;

import io.dataspaceconnector.exception.InvalidEntityException;
import io.dataspaceconnector.model.named.AbstractNamedFactory;
import io.dataspaceconnector.util.ErrorMessage;
import io.dataspaceconnector.util.MetadataUtils;
import io.dataspaceconnector.util.ValidationUtils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates a subscription.
 */
@Component
public class SubscriptionFactory extends AbstractNamedFactory<Subscription, SubscriptionDesc> {

    /** {@inheritDoc} */
    @Override
    protected Subscription initializeEntity(final SubscriptionDesc desc) {
        return new Subscription();
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if any of the parameters is null.
     * @throws InvalidEntityException   if no valid entity can be created from the description.
     */
    @Override
    public boolean update(final Subscription subscription, final SubscriptionDesc desc) {
        final var hasParentUpdated = super.update(subscription, desc);
        final var hasUpdatedUrl = this.updateLocation(subscription, desc.getLocation());
        final var hasUpdatedTarget = this.updateTarget(subscription, desc.getTarget());
        final var hasUpdatedPushData = this.updatePushData(subscription, desc.isPushData());
        final var hasUpdatedSubscriber = this.updateSubscriber(subscription, desc.getSubscriber());
        final var hasUpdateIdsValue = this.updateIdsValue(subscription, desc.isIdsProtocol());

        return hasParentUpdated || hasUpdatedUrl || hasUpdatedTarget || hasUpdatedPushData
               || hasUpdatedSubscriber || hasUpdateIdsValue;
    }

    /**
     * Updates the URL of a subscription.
     *
     * @param subscription The subscription.
     * @param url          The new URL.
     * @return true, if the URL was updated; false otherwise.
     */
    private boolean updateLocation(final Subscription subscription, final URI url) {
        // TODO Should not throw?
        if (url == null || ValidationUtils.isInvalidUri(String.valueOf(url))) {
            throw new InvalidEntityException(ErrorMessage.INVALID_ENTITY_INPUT.toString());
        }

        final var newUri = MetadataUtils.updateUri(subscription.getLocation(), url, null);
        newUri.ifPresent(subscription::setLocation);

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
            throw new InvalidEntityException(ErrorMessage.INVALID_ENTITY_INPUT.toString());
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
            throw new InvalidEntityException(ErrorMessage.INVALID_ENTITY_INPUT.toString());
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
     * @return true, if the value was updated; false otherwise.
     */
    private boolean updatePushData(final Subscription subscription, final boolean push) {
        final var newPushValue =
                MetadataUtils.updateBoolean(subscription.isPushData(), push, false);
        newPushValue.ifPresent(subscription::setPushData);

        return newPushValue.isPresent();
    }

    /**
     * Updates the ids value of a subscription.
     *
     * @param subscription The subscription.
     * @param ids          The new ids value.
     * @return true, if the value was updated; false otherwise.
     */
    private boolean updateIdsValue(final Subscription subscription, final boolean ids) {
        final var newIdsValue =
                MetadataUtils.updateBoolean(subscription.isIdsProtocol(), ids, false);
        newIdsValue.ifPresent(subscription::setIdsProtocol);

        return newIdsValue.isPresent();
    }

}
