package de.fraunhofer.isst.dataspaceconnector.services.resource;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * OfferedResourceRepository interface.
 */
@Repository
public interface OfferedResourceRepository extends JpaRepository<OfferedResource, UUID> {

}
