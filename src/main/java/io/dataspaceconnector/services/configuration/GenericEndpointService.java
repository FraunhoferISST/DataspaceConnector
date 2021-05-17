package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.GenericEndpoint;
import io.dataspaceconnector.model.GenericEndpointDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for generic endpoints.
 */
@Service
@NoArgsConstructor
public class GenericEndpointService extends BaseEntityService<GenericEndpoint, GenericEndpointDesc> {
}
