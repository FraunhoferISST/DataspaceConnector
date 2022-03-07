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

import io.dataspaceconnector.model.base.Entity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for entities identifiable by remote id.
 *
 * @param <T> Type of the entity.
 */
@NoRepositoryBean
public interface RemoteEntityRepository<T extends Entity> extends BaseEntityRepository<T> {
    /*
        NOTE: Maybe return the complete object? Depends on the general usage and the bandwidth
        needed for the Object.
     */

    /**
     * Find an entity id by its remote id.
     *
     * @param remoteId The remote id.
     * @return The id of the entity.
     */
    @Query("SELECT a.id "
            + "FROM #{#entityName} a "
            + "WHERE a.remoteId = :remoteId "
            + "AND a.deleted = false")
    Optional<UUID> identifyByRemoteId(URI remoteId);
}
