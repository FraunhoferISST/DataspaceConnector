package de.fraunhofer.isst.dataspaceconnector.repositories;

import java.util.List;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import org.springframework.data.jpa.repository.Query;

/**
 * The repository containing all objects of type {@link Artifact}.
 */
public interface ArtifactRepository extends BaseEntityRepository<Artifact> {
//    @Query("select a " +
//            "FROM OfferedResource o " +
//            "                INNER JOIN o.representations r " +
//            "                INNER JOIN Artifact a " +
//            "WHERE a member of r.artifacts and r member of o.representations and o.id = :id")
//    List<Artifact> findByResourceId(UUID id);

    @Query("select a from Artifact a, Representation p, OfferedResource r where r.id = :id and p member of r.representations and a member of p.artifacts")
    List<Artifact> findByResourceId(UUID id);
}
