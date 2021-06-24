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

import io.dataspaceconnector.model.base.Factory;
import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Creates and updates a broker.
 */
@Component
public class BrokerFactory implements Factory<Broker, BrokerDesc> {

    /**
     * Default access url.
     */
    private static final URI DEFAULT_URI = URI.create("https://broker.com");

    /**
     * Default string value.
     */
    private static final String DEFAULT_STRING = "unknown";

    /**
     * @param desc The description of the entity.
     * @return The new broker entity.
     */
    @Override
    public Broker create(final BrokerDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var broker = new Broker();
        broker.setOfferedResources(new ArrayList<>());

        update(broker, desc);
        return broker;
    }

    /**
     * @param broker The entity to be updated.
     * @param desc   The description of the new entity.
     * @return True, if broker is updated.
     */
    @Override
    public boolean update(final Broker broker, final BrokerDesc desc) {
        Utils.requireNonNull(broker, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var newAccessUrl = updateAccessUrl(broker, broker.getAccessUrl());
        final var newTitle = updateTitle(broker, broker.getTitle());
        final var newStatus = updateRegistrationStatus(broker, broker.getStatus());
        final var newAdditional = updateAdditional(broker, broker.getAdditional());

        return newAccessUrl || newTitle || newStatus || newAdditional;
    }

    /**
     * @param broker The entity to be updated.
     * @param status The registration status of the broker.
     * @return True, if broker is updated.
     */
    private boolean updateRegistrationStatus(final Broker broker, final RegistrationStatus status) {
        broker.setStatus(Objects.requireNonNullElse(status, RegistrationStatus.UNREGISTERED));
        return true;
    }

    /**
     * @param broker The entity to be updated.
     * @param title  The new title of the entity.
     * @return True, if broker is updated
     */
    private boolean updateTitle(final Broker broker, final String title) {
        final var newTitle = MetadataUtils.updateString(broker.getTitle(), title,
                DEFAULT_STRING);
        newTitle.ifPresent(broker::setTitle);
        return newTitle.isPresent();
    }

    /**
     * @param broker    The entity to be updated.
     * @param accessUrl The new access url of the entity.
     * @return True, if broker is updated.
     */
    private boolean updateAccessUrl(final Broker broker, final URI accessUrl) {
        final var newAccessUrl = MetadataUtils.updateUri(broker.getAccessUrl(), accessUrl,
                DEFAULT_URI);
        newAccessUrl.ifPresent(broker::setAccessUrl);
        return newAccessUrl.isPresent();
    }

    /**
     * @param broker     The entity to be updated.
     * @param additional The updated additional.
     * @return True, if additional is updated.
     */
    private boolean updateAdditional(final Broker broker, final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                broker.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(broker::setAdditional);

        return newAdditional.isPresent();
    }
}
