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
package io.dataspaceconnector.repository;

import io.dataspaceconnector.model.base.RegistrationStatus;
import io.dataspaceconnector.model.broker.Broker;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for brokers.
 */
@Repository
public interface BrokerRepository extends BaseEntityRepository<Broker> {

    /**
     * Finds the broker by the location.
     *
     * @param location The uri of the broker.
     * @return The uuid of the broker.
     */
    @Query("SELECT a.id "
            + "FROM #{#entityName} a "
            + "WHERE a.location = :location "
            + "AND a.deleted = false")
    Optional<UUID> findByLocation(URI location);


    /**
     * Change broker registration state.
     *
     * @param location The uri of the broker.
     * @param status   The uuid of the broker.
     */
    @Transactional
    @Modifying
    @Query("UPDATE Broker a "
            + "SET a.status = :status "
            + "WHERE a.location = :location "
            + "AND a.deleted = false")
    void setRegistrationStatus(URI location, RegistrationStatus status);
}
