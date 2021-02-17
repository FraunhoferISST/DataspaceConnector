package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtifactRepository extends BaseEntityRepository<Artifact> {
}
