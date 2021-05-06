package io.dataspaceconnector.repositories;

import java.util.List;
import java.util.UUID;

import io.dataspaceconnector.model.Catalog;
import io.dataspaceconnector.model.Contract;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * The repository containing all objects of type {@link
 * Catalog}.
 */
@Repository
public interface ContractRepository extends BaseEntityRepository<Contract> {

    /**
     * Finds all contracts applicable for a specific artifact.
     *
     * @param artifactId ID of the artifact
     * @return list of contracts applicable for the artifact
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
