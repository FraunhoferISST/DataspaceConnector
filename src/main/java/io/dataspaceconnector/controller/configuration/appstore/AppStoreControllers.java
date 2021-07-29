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
package io.dataspaceconnector.controller.configuration.appstore;

import io.dataspaceconnector.controller.resource.BaseResourceController;
import io.dataspaceconnector.controller.resource.exception.MethodNotAllowed;
import io.dataspaceconnector.controller.resource.swagger.response.ResponseCode;
import io.dataspaceconnector.controller.resource.swagger.response.ResponseDescription;
import io.dataspaceconnector.controller.resource.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.tag.ResourceName;
import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.app.AppDesc;
import io.dataspaceconnector.model.appstore.AppStore;
import io.dataspaceconnector.model.appstore.AppStoreDesc;
import io.dataspaceconnector.service.appstore.AppStoreRegistryService;
import io.dataspaceconnector.service.configuration.AppService;
import io.dataspaceconnector.service.configuration.AppStoreService;
import io.dataspaceconnector.view.app.AppView;
import io.dataspaceconnector.view.appstore.AppStoreView;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller class for app store management.
 */
public final class AppStoreControllers {
    /**
     * Offers the endpoints for managing apps.
     */
    @RestController
    @RequestMapping("/api/apps")
    @Tag(name = ResourceName.APPS, description = ResourceDescription.APPS)
    public static class AppController extends BaseResourceController<App, AppDesc,
            AppView, AppService> {

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                description = ResponseDescription.METHOD_NOT_ALLOWED)})
        public final ResponseEntity<AppView> create(final AppDesc desc) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                description = ResponseDescription.METHOD_NOT_ALLOWED)})
        public final ResponseEntity<AppView> update(final UUID resourceId, final AppDesc desc) {
            throw new MethodNotAllowed();
        }
    }

    /**
     * Offers the endpoints for managing app stores.
     */
    @RestController
    @RequestMapping("/api/appstore")
    @Tag(name = ResourceName.APPSTORE, description = ResourceDescription.APPSTORE)
    public static class AppStoreController extends BaseResourceController<AppStore, AppStoreDesc,
            AppStoreView, AppStoreService> {
    }

    /**
     * Offers the endpoints for managing app stores.
     */
    @RestController
    @RequestMapping("/api/appstore/registry")
    @Tag(name = ResourceName.APPSTORE_REGISTRY, description = ResourceDescription.APPSTORE_REGISTRY)
    @RequiredArgsConstructor
    public static class AppStoreRegistryController {

        /**
         * App store registry service.
         */
        private final @NonNull AppStoreRegistryService appStoreRegistryService;

        /**
         * @return List of images.
         */
        @GetMapping("/images")
        public final ResponseEntity<String> getImages() {
            String imagesList = appStoreRegistryService.getImages();
            return ResponseEntity.ok(imagesList);
        }

        /**
         * @return List of containers.
         */
        @GetMapping("/containers")
        public final ResponseEntity<String> getContainers() {
            String containerList = appStoreRegistryService.getContainers();
            return ResponseEntity.ok(containerList);
        }
    }
}
