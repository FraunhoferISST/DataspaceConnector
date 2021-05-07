package io.dataspaceconnector.services.resources;

import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.OfferedResourceDesc;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Handles the basic logic for offered resources.
 */
@Service
@NoArgsConstructor
public class OfferedResourceService extends ResourceService<OfferedResource, OfferedResourceDesc> {
}
