package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.AppEndpoint;
import io.dataspaceconnector.model.AppEndpointDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for app endpoints.
 */
@Service
@NoArgsConstructor
public class AppEndpointService extends BaseEntityService<AppEndpoint, AppEndpointDesc> {
}
