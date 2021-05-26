package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.IdsEndpoint;
import io.dataspaceconnector.model.IdsEndpointDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for ids endpoints.
 */
@Service
@NoArgsConstructor
public class IdsEndpointService extends BaseEntityService<IdsEndpoint, IdsEndpointDesc> {
}
