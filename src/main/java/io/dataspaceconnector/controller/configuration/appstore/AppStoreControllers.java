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
import io.dataspaceconnector.service.appstore.container.ContainerConfiguration;
import io.dataspaceconnector.service.configuration.AppService;
import io.dataspaceconnector.service.configuration.AppStoreService;
import io.dataspaceconnector.view.app.AppView;
import io.dataspaceconnector.view.appstore.AppStoreView;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
    @RequestMapping("/api/appstores")
    @Tag(name = ResourceName.APPSTORES, description = ResourceDescription.APPSTORE)
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
        @Operation(summary = "Display all images", description = "Can be used for "
                + "displaying all images.")
        @ApiResponse(responseCode = "200", description = "Ok")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @ApiResponse(responseCode = "500", description = "Internal server error")
        @ResponseBody
        public final ResponseEntity<String> getImages() {
            try {
                final var response = appStoreRegistryService.getImages();
                if (response.isSuccessful()) {
                    return ResponseEntity.ok(response.body().string());
                } else {
                    return ResponseEntity.internalServerError().body(response.body().string());
                }
            } catch (IOException exception) {
                return ResponseEntity.badRequest().body(exception.getMessage());
            }
        }

        /**
         * @return List of containers.
         */
        @GetMapping("/containers")
        @Operation(summary = "Display all containers", description = "Can be used for "
                + "displaying all containers.")
        @ApiResponse(responseCode = "200", description = "Ok")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @ApiResponse(responseCode = "500", description = "Internal server error")
        @ResponseBody
        public final ResponseEntity<String> getContainers() {
            try {
                final var response = appStoreRegistryService.getContainers();
                if (response.isSuccessful()) {
                    return ResponseEntity.ok(response.body().string());
                } else {
                    return ResponseEntity.internalServerError().body(response.body().string());
                }
            } catch (IOException exception) {
                return ResponseEntity.badRequest().body(exception.getMessage());
            }
        }

        /**
         * @param imageName The name of the image.
         * @return Response message, whether image has been downloaded successfully or not.
         */
        @PostMapping("/images/pull")
        @Operation(summary = "Pull an image from registry", description = "Can be used for "
                + "pulling an specific image from the registry.")
        @ApiResponse(responseCode = "200", description = "Ok")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @ApiResponse(responseCode = "500", description = "Internal server error")
        @ResponseBody
        public final ResponseEntity<String> pullImage(
                final @RequestParam("imageName") String imageName) {
            try {
                final var response = appStoreRegistryService.pullImage(imageName);
                if (response.isSuccessful()) {
                    return ResponseEntity.ok("Image successfully downloaded from registry");
                } else {
                    final var message = response.body().string();
                    return ResponseEntity.internalServerError().body(message);
                }
            } catch (IOException exception) {
                return ResponseEntity.badRequest().body(exception.getMessage());
            }
        }

        /**
         * @param containerName          The name of the container.
         * @param containerConfiguration The container configuration-
         * @return Response message, whether container has been created successfully or not.
         */
        @PostMapping("/containers/create")
        @Operation(summary = "Creates a container", description = "Can be used for "
                + "creating a container from a specific image.")
        @ApiResponse(responseCode = "200", description = "Ok")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @ApiResponse(responseCode = "500", description = "Internal server error")
        @ResponseBody
        public final ResponseEntity<String> createContainer(
                final @RequestParam("containerName") String containerName,
                final @RequestBody ContainerConfiguration containerConfiguration) {
            try {
                final var response = appStoreRegistryService
                        .createContainer(containerName, containerConfiguration);
                if (response.isSuccessful()) {
                    return ResponseEntity.ok(response.body().string());
                } else {
                    return ResponseEntity.internalServerError().body(response.body().string());
                }
            } catch (IOException exception) {
                return ResponseEntity.badRequest().body(exception.getMessage());
            }
        }

        /**
         * @param containerId The id of the container.
         * @return Response message, whether container has been started successfully or not.
         */
        @PostMapping("/containers/{id}/start")
        @Operation(summary = "Starts a container", description = "Can be used for "
                + "starting a specific container.")
        @ApiResponse(responseCode = "200", description = "Ok")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @ApiResponse(responseCode = "500", description = "Internal server error")
        @ResponseBody
        public final ResponseEntity<String> startContainer(
                final @PathVariable("id") String containerId) {
            try {
                final var response = appStoreRegistryService
                        .startContainer(containerId);
                if (response.isSuccessful()) {
                    return ResponseEntity.ok(response.body().string());
                } else {
                    return ResponseEntity.internalServerError().body(response.body().string());
                }
            } catch (IOException exception) {
                return ResponseEntity.badRequest().body(exception.getMessage());
            }
        }

        /**
         * @param containerId The id of the container.
         * @return Response message, whether container has been stopped successfully or not.
         */
        @PostMapping("/containers/{id}/stop")
        @Operation(summary = "Stops a container", description = "Can be used for "
                + "stopping a specific container.")
        @ApiResponse(responseCode = "200", description = "Ok")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @ApiResponse(responseCode = "500", description = "Internal server error")
        @ResponseBody
        public final ResponseEntity<String> stopContainer(
                final @PathVariable("id") String containerId) {
            try {
                final var response = appStoreRegistryService
                        .stopContainer(containerId);
                if (response.isSuccessful()) {
                    return ResponseEntity.ok(response.body().string());
                } else {
                    return ResponseEntity.internalServerError().body(response.body().string());
                }
            } catch (IOException exception) {
                return ResponseEntity.badRequest().body(exception.getMessage());
            }
        }

        /**
         * @param containerId The id of the container.
         * @return Response message, whether container has been deleted successfully or not.
         */
        @DeleteMapping("/containers/{id}")
        @Operation(summary = "Deletes a container", description = "Can be used for "
                + "deleting a specific container.")
        @ApiResponse(responseCode = "200", description = "Ok")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @ApiResponse(responseCode = "500", description = "Internal server error")
        @ResponseBody
        public final ResponseEntity<String> deleteContainer(
                final @PathVariable("id") String containerId) {
            try {
                final var response = appStoreRegistryService
                        .deleteContainer(containerId);
                if (response.isSuccessful()) {
                    return ResponseEntity.ok(response.body().string());
                } else {
                    return ResponseEntity.internalServerError().body(response.body().string());
                }
            } catch (IOException exception) {
                return ResponseEntity.badRequest().body(exception.getMessage());
            }
        }
    }
}
