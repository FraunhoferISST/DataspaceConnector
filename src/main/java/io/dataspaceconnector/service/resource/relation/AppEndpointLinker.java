package io.dataspaceconnector.service.resource.relation;

import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.endpoint.AppEndpoint;
import io.dataspaceconnector.service.resource.base.OwningRelationService;
import io.dataspaceconnector.service.resource.type.AppEndpointService;
import io.dataspaceconnector.service.resource.type.AppService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles the relation between apps and app endpoints.
 */
@Service
@NoArgsConstructor
public class AppEndpointLinker extends OwningRelationService<App, AppEndpoint, AppService,
        AppEndpointService> {

    @Override
    protected final List<AppEndpoint> getInternal(final App owner) {
        return owner.getEndpoints();
    }
}
