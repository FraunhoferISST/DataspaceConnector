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
package io.dataspaceconnector.repository;

import io.dataspaceconnector.model.configuration.Configuration;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    @Query("SELECT a.id "
            + "FROM #{#entityName} a "
            + "WHERE a.selected IS NOT NULL "
            + "AND a.deleted = false")
    List<UUID> findBySelectedTrue();

    /**
     * Deselect current configuration.
     */
    @Modifying
    @Query("UPDATE Configuration a "
            + "SET a.selected = NULL "
            + "WHERE a.selected IS NOT NULL "
            + "AND a.deleted = false")
    void deselectCurrent();

    /**
     * Select new configuration.
     *
     * @param uuid uuid to select
     */
    @Modifying
    @Query("UPDATE Configuration a "
            + "SET a.selected = 'SELECTED' "
            + "WHERE a.id = :uuid "
            + "AND a.deleted = false")
    void selectById(UUID uuid);
}
