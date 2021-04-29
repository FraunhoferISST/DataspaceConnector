package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceDesc;
import lombok.NoArgsConstructor;

/**
 * Handles the basic logic for resources.
 *
 * @param <T> The resource type.
 * @param <D> The resource description type.
 */
@NoArgsConstructor
public class ResourceService<T extends Resource, D extends ResourceDesc<T>>
        extends BaseEntityService<T, D> {
}
