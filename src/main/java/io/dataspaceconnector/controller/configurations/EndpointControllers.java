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

import io.dataspaceconnector.controller.resources.BaseResourceController;
import io.dataspaceconnector.model.AppEndpoint;
import io.dataspaceconnector.model.AppEndpointDesc;
import io.dataspaceconnector.model.GenericEndpoint;
import io.dataspaceconnector.model.GenericEndpointDesc;
import io.dataspaceconnector.model.IdsEndpoint;
import io.dataspaceconnector.model.IdsEndpointDesc;
import io.dataspaceconnector.services.configuration.AppEndpointService;
import io.dataspaceconnector.services.configuration.GenericEndpointService;
import io.dataspaceconnector.services.configuration.IdsEndpointService;
import io.dataspaceconnector.view.AppEndpointView;
import io.dataspaceconnector.view.GenericEndpointView;
import io.dataspaceconnector.view.IdsEndpointView;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for management of different endpoints.
 */
public final class EndpointControllers {

    /**
     * Offers the endpoints for managing generic endpoints.
     */
    @RestController
    @RequestMapping("/api/genericendpoints")
    @Tag(name = "Generic Endpoint", description = "Endpoints for CRUD operations on"
            + " generic endpoints")
    public static class GenericEndpointController
            extends BaseResourceController<GenericEndpoint, GenericEndpointDesc,
            GenericEndpointView, GenericEndpointService> {
    }

    /**
     * Offers the endpoints for managing ids endpoints.
     */
    @RestController
    @RequestMapping("/api/idsendpoints")
    @Tag(name = "Ids Endpoint", description = "Endpoints for CRUD operations on"
            + " ids endpoints")
    public static class IdsEndpointController
            extends BaseResourceController<IdsEndpoint, IdsEndpointDesc,
            IdsEndpointView, IdsEndpointService> {

    }

    /**
     * Offers the endpoints for managing apps.
     */
    @RestController
    @RequestMapping("/api/appendpoints")
    @Tag(name = "App Endpoints", description = "Endpoints for CRUD operations on app endpoints")
    public static class AppEndpointController
            extends BaseResourceController<AppEndpoint, AppEndpointDesc, AppEndpointView,
            AppEndpointService> {
    }
}
