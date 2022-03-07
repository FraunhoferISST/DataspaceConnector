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

import io.dataspaceconnector.model.artifact.Artifact;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * The repository containing all objects of type {@link Artifact}.
 */
@Repository
public interface ArtifactRepository extends RemoteEntityRepository<Artifact> {

    /**
     * Finds all artifacts of a specific resource.
     *
     * @param resourceId The resource's id.
     * @return List of all artifacts of the resource.
     */
    @Query("SELECT a "
            + "FROM Artifact a, Representation r, OfferedResource o "
            + "WHERE o.id = :resourceId "
            + "AND r MEMBER OF o.representations "
            + "AND a MEMBER OF r.artifacts "
            + "AND a.deleted = false "
            + "AND r.deleted = false "
            + "AND o.deleted = false")
    List<Artifact> findAllByResourceId(UUID resourceId);

    /**
     * Finds all artifacts referenced in a specific agreement.
     *
     * @param agreementId The id of the agreement.
     * @return List of all artifacts referenced in the agreement.
     */
    @Query("SELECT a "
            + "FROM Artifact a INNER JOIN Agreement ag ON a MEMBER OF ag.artifacts "
            + "WHERE ag.id = :agreementId "
            + "AND ag.deleted = false "
            + "AND a.deleted = false")
    List<Artifact> findAllByAgreement(UUID agreementId);

    /**
     * Search for all agreements signed for requested resources by this connector as consumer.
     *
     * @param artifactId The artifact.
     * @return The list of agreement ids.
     */
    @Query("SELECT ag.remoteId "
            + "FROM Artifact a, Agreement ag "
            + "WHERE a.id = :artifactId "
            + "AND a.deleted = false "
            + "AND ag.deleted = false "
            + "AND ag.remoteId <> 'genesis' "
            + "AND ag.archived = false "
            + "AND ag.confirmed = true "
            + "AND ag MEMBER OF a.agreements")
    List<URI> findRemoteOriginAgreements(UUID artifactId);

    /**
     * Set the artifacts data.
     *
     * @param artifactId The artifact.
     * @param checkSum   The new CRC32C checksum.
     * @param size       The new size in bytes.
     */
    @Transactional
    @Modifying
    @Query("UPDATE Artifact a "
            + "SET a.checkSum=:checkSum, a.byteSize=:size "
            + "WHERE a.id = :artifactId "
            + "AND a.deleted = false")
    void setArtifactData(UUID artifactId, long checkSum, long size);

    /**
     * Finds all artifacts with a specific bootstrap ID.
     *
     * @param bootstrapId The bootstrap id of the artifact.
     * @return A list of all artifacts with given bootstrap id.
     */
    @Query("SELECT a "
            + "FROM Artifact a "
            + "WHERE a.bootstrapId = :bootstrapId "
            + "AND a.deleted = false")
    List<Artifact> findAllByBootstrapId(URI bootstrapId);
}
