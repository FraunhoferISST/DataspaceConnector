package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface to the repository containing the requested resources.
 */
@Repository
public interface RequestedResourceRepository extends JpaRepository<RequestedResource, UUID> {

}
