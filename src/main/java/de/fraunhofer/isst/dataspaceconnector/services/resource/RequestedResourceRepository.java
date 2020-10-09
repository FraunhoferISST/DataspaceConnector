package de.fraunhofer.isst.dataspaceconnector.services.resource;

import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * <p>RequestedResourceRepository interface.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Repository
public interface RequestedResourceRepository extends JpaRepository<RequestedResource, UUID> {
}
