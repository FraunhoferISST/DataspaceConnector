package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import org.springframework.stereotype.Repository;

/**
 * The repository containing all objects of type {@link Representation}.
 */
@Repository
public interface RepresentationRepository extends RemoteEntityRepository<Representation> {
}
