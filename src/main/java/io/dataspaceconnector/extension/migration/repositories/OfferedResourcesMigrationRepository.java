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
package io.dataspaceconnector.extension.migration.repositories;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository for migrating offered resources.
 */
@ConditionalOnProperty(value = "migration.enabled", havingValue = "true")
@Repository
public interface OfferedResourcesMigrationRepository
        extends io.dataspaceconnector.repository.OfferedResourcesRepository {

    /**
     * Migrate the license field.
     */
    @Modifying
    @Query("UPDATE OfferedResource a "
           + "SET a.license = a.licence "
           + "WHERE a.license IS NULL "
           + "AND a.licence IS NOT NULL")
    void migrateV5ToV6();
}
