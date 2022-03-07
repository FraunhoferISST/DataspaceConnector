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
package io.dataspaceconnector.model.broker;

import io.dataspaceconnector.model.base.RegistrationStatus;
import io.dataspaceconnector.model.named.AbstractNamedFactory;
import io.dataspaceconnector.model.util.FactoryUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Creates and updates a broker.
 */
public class BrokerFactory extends AbstractNamedFactory<Broker, BrokerDesc> {

    /**
     * Default access url.
     */
    public static final URI DEFAULT_URI = URI.create("https://broker.com");

    /**
     * @param desc The description of the entity.
     * @return The new broker entity.
     */
    @Override
    protected Broker initializeEntity(final BrokerDesc desc) {
        final var broker = new Broker();
        broker.setOfferedResources(new ArrayList<>());

        return broker;
    }

    /**
     * @param broker The entity to be updated.
     * @param desc   The description of the new entity.
     * @return True, if broker is updated.
     */
    @Override
    protected boolean updateInternal(final Broker broker, final BrokerDesc desc) {
        final var newLocation = updateLocation(broker, desc.getLocation());
        final var newStatus = updateRegistrationStatus(broker, desc.getStatus());

        return newLocation || newStatus;
    }

    /**
     * @param broker The entity to be updated.
     * @param status The registration status of the broker.
     * @return True, if broker is updated.
     */
    private boolean updateRegistrationStatus(final Broker broker, final RegistrationStatus status) {
        if (broker.getStatus() != null && broker.getStatus() == status) {
            return false;
        }

        broker.setStatus(Objects.requireNonNullElse(status, RegistrationStatus.UNREGISTERED));
        return true;
    }

    /**
     * @param broker   The entity to be updated.
     * @param location The new location url of the entity.
     * @return True, if broker is updated.
     */
    private boolean updateLocation(final Broker broker, final URI location) {
        final var newAccessUrl = FactoryUtils.updateUri(broker.getLocation(), location,
                DEFAULT_URI);
        newAccessUrl.ifPresent(broker::setLocation);
        return newAccessUrl.isPresent();
    }
}
