package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * The repository containing all objects of type {@link Representation}.
 */
@RepositoryRestResource(collectionResourceRel = "representations", path = "representations")
public interface RepresentationRepository extends BaseEntityRepository<Representation> {
}
