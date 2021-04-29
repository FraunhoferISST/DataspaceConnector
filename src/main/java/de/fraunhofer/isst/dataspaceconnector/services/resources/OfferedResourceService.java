package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Handles the basic logic for offered resources.
 */
@Service
@NoArgsConstructor
public class OfferedResourceService extends ResourceService<OfferedResource, OfferedResourceDesc> {
}
