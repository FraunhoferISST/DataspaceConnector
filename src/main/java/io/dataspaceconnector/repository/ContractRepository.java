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

import io.dataspaceconnector.model.contract.Contract;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * The repository containing all objects of type {@link
 * io.dataspaceconnector.model.catalog.Catalog}.
 */
@Repository
public interface ContractRepository extends BaseEntityRepository<Contract> {

    /**
     * Finds all contracts applicable for a specific artifact.
     *
     * @param artifactId ID of the artifact.
     * @return list of contracts applicable for the artifact.
     */
    @Query("SELECT c "
            + "FROM Contract c INNER JOIN OfferedResource o ON c MEMBER OF o.contracts "
            + "INNER JOIN Representation r ON r MEMBER OF o.representations "
            + "INNER JOIN Artifact a ON a MEMBER OF r.artifacts WHERE a.id = :artifactId "
            + "AND c.deleted = false "
            + "AND o.deleted = false "
            + "AND r.deleted = false "
            + "AND a.deleted = false")
    List<Contract> findAllByArtifactId(UUID artifactId);
}
