package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Interface to the repository containing the offered resources.
 */
@Repository
public interface OfferedResourceRepository extends JpaRepository<OfferedResource, UUID> {

}
