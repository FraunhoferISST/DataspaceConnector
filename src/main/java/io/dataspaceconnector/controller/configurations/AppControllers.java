package io.dataspaceconnector.controller.configurations;

import io.dataspaceconnector.controller.resources.BaseResourceChildController;
import io.dataspaceconnector.controller.resources.BaseResourceController;
import io.dataspaceconnector.model.*;
import io.dataspaceconnector.services.configuration.AppEndpointService;
import io.dataspaceconnector.services.configuration.AppService;
import io.dataspaceconnector.services.configuration.AppStoreService;
import io.dataspaceconnector.services.configuration.EntityLinkerService;
import io.dataspaceconnector.view.AppEndpointView;
import io.dataspaceconnector.view.AppStoreView;
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

    /**
     * Offers the endpoints for managing app stores.
     */
    @RestController
    @RequestMapping("/api/appstores")
    @Tag(name = "App Store", description = "Endpoints for CRUD operations on app store")
    public static class AppStoreController
            extends BaseResourceController<AppStore, AppStoreDesc, AppStoreView, AppStoreService> {
    }

    /**
     * Offers the endpoints for managing the relations between app store and apps.
     */
    @RestController
    @RequestMapping("/api/appstores/{id}/apps")
    @Tag(name = "App Store", description = "Endpoints for linking app stores to apps")
    public static class AppStoreToApps extends BaseResourceChildController<
            EntityLinkerService.AppStoreAppLinker, App, AppView> {
    }
}
