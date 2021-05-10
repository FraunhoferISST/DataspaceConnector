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
package io.dataspaceconnector.repositories;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import io.dataspaceconnector.model.Artifact;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * The repository containing all objects of type {@link Artifact}.
 */
@Repository
public interface ArtifactRepository extends RemoteEntityRepository<Artifact> {

    /**
     * Finds all artifacts of a specific resource.
     *
     * @param resourceId ID of the resource
     * @return list of all artifacts of the resource
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
     * @param agreementId ID of the agreement
     * @return list of all artifacts referenced in the agreement
     */
    @Query("SELECT a "
            + "FROM Artifact a INNER JOIN Agreement ag ON a MEMBER OF ag.artifacts "
            + "WHERE ag.id = :agreementId "
            + "AND ag.deleted = false "
            + "AND a.deleted = false")
    List<Artifact> findAllByAgreement(UUID agreementId);

    /**
     * Search for all agreements signed for requested resources by this connector as consumer.
     * @param artifactId The artifact.
     * @return The list of agreement ids.
     */
    @Query("SELECT ag.remoteId "
            + "FROM Artifact a, Agreement ag "
            + "WHERE a.id = :artifactId "
            + "AND a.deleted = false "
            + "AND ag.deleted = false "
            + "AND ag.remoteId <> 'aced00057372000c6a6176612e6e65742e555"
                + "249ac01782e439e49ab0300014c0006737472696e677400124c6a"
                + "6176612f6c616e672f537472696e673b787074000767656e6573697378' "
            + "AND ag.archived = false "
            + "AND ag.confirmed = true "
            + "AND ag MEMBER OF a.agreements")
    List<URI> findRemoteOriginAgreements(UUID artifactId);

    /**
     * Set the artifacts data.
     * @param artifactId The artifact.
     * @param checkSum The new CRC32C checksum.
     * @param size The new size in bytes.
     */
    @Modifying
    @Query("UPDATE Artifact a "
            + "SET a.checkSum=:checkSum, a.byteSize=:size "
            + "WHERE a.id = :artifactId "
            + "AND a.deleted = false")
    void setArtifactData(UUID artifactId, long checkSum, long size);
}
