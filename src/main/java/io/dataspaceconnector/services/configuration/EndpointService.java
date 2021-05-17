package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.Endpoint;
import io.dataspaceconnector.model.EndpointDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for endpoints.
 */
@Service
@NoArgsConstructor
public class EndpointService<T extends Endpoint, D extends EndpointDesc<T>>
        extends BaseEntityService<T, D> {
}
