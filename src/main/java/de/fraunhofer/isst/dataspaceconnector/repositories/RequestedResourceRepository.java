package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Interface to the repository containing the requested resources.
 */
@Repository
public interface RequestedResourceRepository extends JpaRepository<RequestedResource, UUID> {

}
