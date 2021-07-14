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
package io.dataspaceconnector.service.configuration;

import io.dataspaceconnector.model.base.RegistrationStatus;
import io.dataspaceconnector.model.broker.Broker;
import io.dataspaceconnector.model.broker.BrokerDesc;
import io.dataspaceconnector.model.broker.BrokerFactory;
import io.dataspaceconnector.repository.BrokerRepository;
import io.dataspaceconnector.service.resource.BaseEntityService;
import io.dataspaceconnector.util.ErrorMessages;
import io.dataspaceconnector.util.Utils;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for brokers.
 */
@Service("ConfigurationBrokerService") //Clashes with IDS-Messaging-Services brokerService Bean
@NoArgsConstructor
public class BrokerService extends BaseEntityService<Broker, BrokerDesc> {

    @Override
    public final void delete(final UUID entityId) {
        Utils.requireNonNull(entityId, ErrorMessages.ENTITYID_NULL);
        if (!getRepository().getById(entityId).getOfferedResources().isEmpty()) {
            return;
        }

        super.delete(entityId);
    }

    /**
     * Finds a broker by the uri.
     * @param location The uri of the broker.
     * @return The uuid of the broker.
     */
    public Optional<UUID> findByLocation(final URI location) {
        return ((BrokerRepository) getRepository()).findByLocation(location);
    }

    /**
     * This method updates the registration status of the broker.
     * @param location The uri of the broker.
     */
    public void updateRegistrationStatus(final URI location) {
        final var brokerId = ((BrokerRepository) getRepository()).findByLocation(location);
        if (brokerId.isPresent()) {
            final var broker = getRepository().findById(brokerId.get());
            if (broker.isPresent()) {
                if (RegistrationStatus.UNREGISTERED.equals(broker.get().getStatus())) {
                    ((BrokerFactory) getFactory()).updateRegistrationStatus(broker.get(),
                            RegistrationStatus.REGISTERED);
                } else {
                    ((BrokerFactory) getFactory()).updateRegistrationStatus(broker.get(),
                            RegistrationStatus.UNREGISTERED);
                }
                super.persist(broker.get());
            }
        }
    }
}
