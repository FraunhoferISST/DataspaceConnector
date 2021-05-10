package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.Representation;
import org.springframework.stereotype.Repository;

/**
 * The repository containing all objects of type {@link Representation}.
 */
@Repository
public interface RepresentationRepository extends RemoteEntityRepository<Representation> {
}
