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

import io.dataspaceconnector.model.agreement.Agreement;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * The repository containing all objects of type {@link Agreement}.
 */
@Repository
public interface AgreementRepository extends BaseEntityRepository<Agreement> {

    /**
     * Set the status of an agreement to confirmed.
     *
     * @param entityId The id of the agreement.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Agreement a "
            + "SET a.confirmed = true "
            + "WHERE a.id = :entityId "
            + "AND a.archived = false "
            + "AND a.deleted = false")
    void confirmAgreement(UUID entityId);
}
