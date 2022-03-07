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

import io.dataspaceconnector.model.configuration.Configuration;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for the configuration.
 */
@Repository
public interface ConfigurationRepository extends BaseEntityRepository<Configuration> {

    /**
     * Get all selected configurations.
     *
     * @return UUIDs of Configurations that are marked as selected.
     */
    @Query("SELECT a "
            + "FROM #{#entityName} a "
            + "WHERE a.active = true "
            + "AND a.deleted = false")
    Optional<Configuration> findActive();

    /**
     * Deselect current configuration.
     */
    @Transactional
    @Modifying
    @Query("UPDATE Configuration a "
            + "SET a.active = NULL "
            + "WHERE a.active = true "
            + "AND a.deleted = false")
    void unsetActive();

    /**
     * Select new configuration.
     *
     * @param id Id to select
     */
    @Transactional
    @Modifying
    @Query("UPDATE Configuration a "
            + "SET a.active = true "
            + "WHERE a.id = :id "
            + "AND a.deleted = false")
    void setActive(UUID id);
}
