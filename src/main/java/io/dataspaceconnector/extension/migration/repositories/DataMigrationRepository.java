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

import io.dataspaceconnector.repository.DataRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DataMigrationRepository extends DataRepository {
    @Query("SELECT a.username "
           + "FROM RemoteData a "
           + "WHERE a.id = :id "
           + "AND a.migration <> 'v6' "
           + "AND a.username IS NOT NULL")
    String migrateV5ToV6_getUsername(Long id);

    @Query("SELECT a.password "
           + "FROM RemoteData a "
           + "WHERE a.id = :id "
           + "AND a.migration <> 'v6' "
           + "AND a.password IS NOT NULL")
    String migrateV5Tov6_getPassword(Long id);

    @Modifying
    @Query("UPDATE RemoteData a "
           + "SET a.username = NULL, a.password = NULL, a.migration = 'v6' "
           + "WHERE a.id = :id")
    void migrateV5Tov6_removeUsernameAndPassword(Long id);
}
