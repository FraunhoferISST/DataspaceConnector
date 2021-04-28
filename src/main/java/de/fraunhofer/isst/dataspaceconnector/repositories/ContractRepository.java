package de.fraunhofer.isst.dataspaceconnector.repositories;

import java.util.List;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * The repository containing all objects of type {@link
 * de.fraunhofer.isst.dataspaceconnector.model.Catalog}.
 */
@Repository
public interface ContractRepository extends BaseEntityRepository<Contract> {

    /**
     * Finds all contracts applicable for a specific artifact.
     *
     * @param artifactId ID of the artifact
     * @return list of contracts applicable for the artifact
     */
    @Query("SELECT c from Contract c INNER JOIN OfferedResource o ON c MEMBER OF o.contracts "
            + "INNER JOIN Representation r ON r MEMBER OF o.representations "
            + "INNER JOIN Artifact a ON a MEMBER OF r.artifacts WHERE a.id = :artifactId")
    List<Contract> findAllByArtifactId(UUID artifactId);

}
