package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface to the repository containing the offered resources.
 */
@Repository
public interface OfferedResourceRepository extends JpaRepository<OfferedResource, UUID> {

}
