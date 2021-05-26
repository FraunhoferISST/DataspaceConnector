package io.dataspaceconnector.controller.configurations;

import io.dataspaceconnector.controller.resources.BaseResourceController;
import io.dataspaceconnector.model.App;
import io.dataspaceconnector.model.AppDesc;
import io.dataspaceconnector.model.AppEndpoint;
import io.dataspaceconnector.model.AppEndpointDesc;
import io.dataspaceconnector.services.configuration.AppEndpointService;
import io.dataspaceconnector.services.configuration.AppService;
import io.dataspaceconnector.view.AppEndpointView;
import io.dataspaceconnector.view.AppView;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class contains controller for app management.
 */
public final class AppControllers {

    /**
     * Offers the endpoints for managing apps.
     */
    @RestController
    @RequestMapping("/api/apps")
    @Tag(name = "Apps", description = "Endpoints for CRUD operations on apps")
    public static class AppController
            extends BaseResourceController<App, AppDesc, AppView, AppService> {
    }

    /**
     * Offers the endpoints for managing apps.
     */
    @RestController
    @RequestMapping("/api/appendpoints")
    @Tag(name = "App Endpoints", description = "Endpoints for CRUD operations on app endpoints")
    public static class AppEndpointController
            extends BaseResourceController<AppEndpoint, AppEndpointDesc, AppEndpointView, AppEndpointService> {
    }
}
