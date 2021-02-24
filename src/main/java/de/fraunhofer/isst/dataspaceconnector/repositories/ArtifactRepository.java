package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.UUID;

/**
 * The repository containing all objects of type {@link Artifact}.
 */
@RepositoryRestResource(collectionResourceRel = "artifacts", path = "artifacts")
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
