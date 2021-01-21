package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * RequestedResourceRepository interface.
 */
@Repository
public interface RequestedResourceRepository extends JpaRepository<RequestedResource, UUID> {

}
