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

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repo for migration agreements.
 */
@ConditionalOnProperty(value = "migration.enabled", havingValue = "true")
@Repository
public interface AgreementMigrationRepository
        extends io.dataspaceconnector.repository.AgreementRepository {

    /**
     * Will update ids agreements.
     * @param entityId The agreement.
     * @param value The new agreement.
     */
    @Modifying
    @Query("UPDATE Agreement a "
           + "SET a.value = :value "
           + "WHERE a.id = :entityId")
    void migrateV5ToV6UpgradeIDS(UUID entityId, String value);
}
