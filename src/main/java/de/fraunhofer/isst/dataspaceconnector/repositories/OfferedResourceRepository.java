package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * OfferedResourceRepository interface.
 */
@Repository
public interface OfferedResourceRepository extends JpaRepository<OfferedResource, UUID> {

}
