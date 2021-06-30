/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.controller.configurations;

import io.dataspaceconnector.controller.resources.BaseResourceChildController;
import io.dataspaceconnector.controller.resources.BaseResourceController;
import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.app.AppDesc;
import io.dataspaceconnector.model.appstore.AppStore;
import io.dataspaceconnector.model.appstore.AppStoreDesc;
import io.dataspaceconnector.services.configuration.AppService;
import io.dataspaceconnector.services.configuration.AppStoreService;
import io.dataspaceconnector.services.configuration.EntityLinkerService;
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
            extends BaseResourceController<App, AppDesc, AppView, AppService> { }

    /**
     * Offers the endpoints for managing app stores.
     */
    @RestController
    @RequestMapping("/api/appstores")
    @Tag(name = "App Store", description = "Endpoints for CRUD operations on app store")
    public static class AppStoreController
            extends BaseResourceController<AppStore, AppStoreDesc, AppStoreView, AppStoreService> { }

    /**
     * Offers the endpoints for managing the relations between app store and apps.
     */
    @RestController
    @RequestMapping("/api/appstores/{id}/apps")
    @Tag(name = "App Store", description = "Endpoints for linking app stores to apps")
    public static class AppStoreToApps extends BaseResourceChildController<
            EntityLinkerService.AppStoreAppLinker, App, AppView> { }
}
