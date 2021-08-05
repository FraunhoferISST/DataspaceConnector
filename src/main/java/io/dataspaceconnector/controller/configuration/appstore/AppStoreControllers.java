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
import io.dataspaceconnector.service.appstore.container.ActionType;
import io.dataspaceconnector.service.configuration.AppService;
import io.dataspaceconnector.service.configuration.AppStoreService;
import io.dataspaceconnector.util.Utils;
import io.dataspaceconnector.view.app.AppView;
import io.dataspaceconnector.view.appstore.AppStoreView;
import io.dataspaceconnector.view.appstore.AppStoreViewAssembler;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
    @RequiredArgsConstructor
    public static class AppController extends BaseResourceController<App, AppDesc,
            AppView, AppService> {

        /**
         * App store registry service.
         */
        private final @NonNull AppStoreRegistryService appStoreRegistryService;

        /**
         * The assembler for creating pages of AppStoreViews.
         */
        @Autowired
        private final PagedResourcesAssembler<AppStore> appStorePagedResourcesAssembler;

        /**
         * The assembler for creating AppStoreViews.
         */
        @Autowired
        private final AppStoreViewAssembler appStoreViewAssembler;

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

        /**
         * @param containerId The id of the container.
         * @param actionType  The action type.
         * @return Response depending on the action on an app.
         */
        @PutMapping("/{id}/actions")
        @Operation(summary = "Actions on apps", description = "Can be used for "
                + "managing apps.")
        @ApiResponse(responseCode = "200", description = "Ok")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @ApiResponse(responseCode = "500", description = "Internal server error")
        @ResponseBody
        public final ResponseEntity<String> containerManagement(
                final @PathVariable("id") String containerId,
                final @RequestParam("actionType") String actionType) {

            final var action = actionType.toUpperCase();
            ResponseEntity<String> response = null;
            try {
                if (ActionType.START.name().equals(action)) {
                    final var startResponse = appStoreRegistryService.startContainer(containerId);
                    if (startResponse.isSuccessful()) {
                        response = ResponseEntity.ok("Successfully started the app.");
                    } else {
                        response = ResponseEntity.internalServerError()
                                .body(startResponse.body().string());
                    }
                }
                if (ActionType.STOP.name().equals(action)) {
                    final var stopResponse = appStoreRegistryService.stopContainer(containerId);
                    if (stopResponse.isSuccessful()) {
                        response = ResponseEntity.ok("Successfully stopped the app.");
                    } else {
                        response = ResponseEntity.internalServerError()
                                .body(stopResponse.body().string());
                    }
                }
                if (ActionType.DELETE.name().equals(action)) {
                    final var deleteResponse = appStoreRegistryService.deleteContainer(containerId);
                    if (deleteResponse.isSuccessful()) {
                        response = ResponseEntity.ok("Successfully deleted the app.");
                    } else {
                        response = ResponseEntity.internalServerError()
                                .body(deleteResponse.body().string());
                    }
                }
            } catch (IOException e) {
                response = ResponseEntity.badRequest().body(e.getMessage());
            }
            return response;
        }

        /**
         * @param containerId The id of the container.
         * @return Response which describes the current app.
         */
        @GetMapping("/{id}/describe")
        @Operation(summary = "Description of the app container", description = "Can be used for "
                + "describing the current app container.")
        @ApiResponse(responseCode = "200", description = "Ok")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @ApiResponse(responseCode = "500", description = "Internal server error")
        @ResponseBody
        public final ResponseEntity<String> containerDescription(
                final @PathVariable("id") String containerId) {
            ResponseEntity<String> response;
            try {
                final var descriptionResponse = appStoreRegistryService
                        .getContainerDescription(containerId);
                if (descriptionResponse.isSuccessful()) {
                    response = ResponseEntity.ok(descriptionResponse.body().string());
                } else {
                    response = ResponseEntity.internalServerError()
                            .body(descriptionResponse.body().string());
                }
            } catch (IOException e) {
                response = ResponseEntity.badRequest().body(e.getMessage());
            }
            return response;
        }

        /**
         * Get the AppStores related to the given app.
         *
         * @param resourceId id of app for which related appstores should be found.
         * @param page number of the page to get.
         * @param size size of response pages.
         * @return Pageable of AppStores.
         */
        @GetMapping("/{id}/appstore")
        @Operation(summary = "Get appstore holding this app",
                description = "Get appstore holding this app"
        )
        @ApiResponse(responseCode = "200", description = "Ok")
        @ApiResponse(responseCode = "400", description = "Bad request")
        @ApiResponse(responseCode = "500", description = "Internal server error")
        @ResponseBody
        public final PagedModel<AppStoreView> relatedAppStore(
                final @PathVariable("id") UUID resourceId,
                final @RequestParam(required = false, defaultValue = "0") Integer page,
                final @RequestParam(required = false, defaultValue = "30") Integer size) {
            final var pageable = Utils.toPageRequest(page, size);
            final var entities = getService()
                    .getStoresByContainsApp(resourceId, pageable);
            if (entities.hasContent()) {
                return appStorePagedResourcesAssembler.toModel(entities, appStoreViewAssembler);
            } else {
                return (PagedModel<AppStoreView>) appStorePagedResourcesAssembler
                        .toEmptyModel(entities, AppStore.class);
            }
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
}
