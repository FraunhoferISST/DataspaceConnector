package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "artifacts", path="artifacts")
public interface ArtifactRepository extends BaseEntityRepository<Artifact> {
}
