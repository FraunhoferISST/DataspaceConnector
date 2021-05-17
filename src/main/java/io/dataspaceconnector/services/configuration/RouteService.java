package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.Route;
import io.dataspaceconnector.model.RouteDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for routes.
 */
@Service
@NoArgsConstructor
public class RouteService extends BaseEntityService<Route, RouteDesc> {
}
