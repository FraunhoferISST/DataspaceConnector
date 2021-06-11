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

import java.net.URI;
import java.util.ArrayList;

import io.dataspaceconnector.exceptions.InvalidEntityException;
import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates a subscriber.
 */
@Component
public class SubscriberFactory implements AbstractFactory<Subscriber, SubscriberDesc> {

    /**
     * Creates a new subscriber.
     *
     * @param desc The description of the new subscriber.
     * @return the new subscriber.
     * @throws IllegalArgumentException if desc is null.
     * @throws InvalidEntityException if no valid entity can be created from the description.
     */
    @Override
    public Subscriber create(final SubscriberDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var subscriber = new Subscriber();
        subscriber.setResources(new ArrayList<>());

        update(subscriber, desc);

        return subscriber;
    }

    /**
     * Updates a subscriber.
     *
     * @param subscriber the subscriber to be updated.
     * @param desc The new subscriber description.
     * @return true, if the subscriber has been modified; false otherwise.
     * @throws IllegalArgumentException if any of the parameters is null.
     * @throws InvalidEntityException if no valid entity can be created from the description.
     */
    @Override
    public boolean update(final Subscriber subscriber, final SubscriberDesc desc) {
        Utils.requireNonNull(subscriber, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        return updateUrl(subscriber, desc.getUrl());
    }

    /**
     * Updates the URL for a subscriber.
     * @param subscriber the subscriber.
     * @param url the new URL.
     * @return true, if the URL was updated; false otherwise.
     */
    private boolean updateUrl(final Subscriber subscriber, final URI url) {
        if (url == null) {
            throw new InvalidEntityException(ErrorMessages.INVALID_ENTITY_INPUT.toString());
        }

        final var newUri = MetadataUtils.updateUri(subscriber.getUrl(), url, null);
        newUri.ifPresent(subscriber::setUrl);

        return newUri.isPresent();
    }

}
