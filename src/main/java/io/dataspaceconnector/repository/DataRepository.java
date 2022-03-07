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

import io.dataspaceconnector.model.artifact.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * The repository containing all objects of type {@link Data}.
 */
@Repository
public interface DataRepository extends JpaRepository<Data, Long> {
    /**
     * Set new local data for an entity.
     *
     * @param entityId The entity id.
     * @param data     The new data.
     */
    @Transactional
    @Modifying
    @Query("UPDATE LocalData a "
            + "SET a.value = :data "
            + "WHERE a.id = :entityId")
    void setLocalData(Long entityId, byte[] data);

    /**
     * Removes a RemoteData object from the database.
     *
     * @param entityId ID of the data to delete.
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM RemoteData r WHERE r.id =:entityId")
    void deleteRemoteData(Long entityId);
}
