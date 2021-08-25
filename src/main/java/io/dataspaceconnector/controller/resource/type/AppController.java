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
package io.dataspaceconnector.controller.resource.type;

import io.dataspaceconnector.common.exception.PortainerNotConfigured;
import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.controller.resource.base.BaseResourceController;
import io.dataspaceconnector.controller.resource.base.exception.MethodNotAllowed;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.app.AppView;
import io.dataspaceconnector.controller.util.ActionType;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.app.AppDesc;
import io.dataspaceconnector.model.app.AppImpl;
import io.dataspaceconnector.service.appstore.portainer.PortainerRequestService;
import io.dataspaceconnector.service.resource.type.AppService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Offers the endpoints for managing apps.
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(BasePath.APPS)
@Tag(name = ResourceName.APPS, description = ResourceDescription.APPS)
public class AppController extends BaseResourceController<App, AppDesc, AppView, AppService> {

    /**
     * Portainer request service.
     */
    private final @NonNull PortainerRequestService portainerSvc;

    @Hidden
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                    description = ResponseDescription.METHOD_NOT_ALLOWED),
            @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                    description = ResponseDescription.UNAUTHORIZED)})
    @Override
    public final ResponseEntity<AppView> create(final AppDesc desc) {
        throw new MethodNotAllowed();
    }

    @Hidden
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                    description = ResponseDescription.METHOD_NOT_ALLOWED),
            @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                    description = ResponseDescription.UNAUTHORIZED)})
    @Override
    public final ResponseEntity<AppView> update(final UUID resourceId, final AppDesc desc) {
        throw new MethodNotAllowed();
    }

    /**
     * @param appId The id of the container.
     * @param type  The action type.
     * @return Response depending on the action on an app.
     */
    @PutMapping("/{id}/actions")
    @Operation(summary = "Actions on apps", description = "Can be used for managing apps.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST,
                    description = ResponseDescription.BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_SERVER_ERROR,
                    description = ResponseDescription.INTERNAL_SERVER_ERROR),
            @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                    description = ResponseDescription.UNAUTHORIZED)})
    @ResponseBody
    public final ResponseEntity<Object> containerManagement(
            @PathVariable("id") final UUID appId,
            @RequestParam("actionType") final String type) {
        final var action = type.toUpperCase();
        final var app = getService().get(appId);
        var containerId = ((AppImpl) app).getContainerId();

        Response response;
        try {
            portainerSvc.createEndpointId();

            if (ActionType.START.name().equals(action)) {
                response = portainerSvc.startContainer(
                        containerId == null || containerId.equals("")
                                ? deployApp(app) : containerId);
                return readResponse(response, "Successfully started the app.");
            } else if (ActionType.STOP.name().equals(action)) {
                if (containerId == null || containerId.equals("")) {
                    return new ResponseEntity<>("No container id provided.",
                            HttpStatus.NOT_FOUND);
                }

                response = portainerSvc.stopContainer(containerId);
                return readResponse(response, "Successfully stopped the app.");
            } else if (ActionType.DELETE.name().equals(action)) {
                if (containerId == null || containerId.equals("")) {
                    return new ResponseEntity<>("No container id provided.",
                            HttpStatus.NOT_FOUND);
                }

                response = portainerSvc.deleteContainer(containerId);
                // getService().deleteContainerIdFromApp(appId); // TODO ???
                portainerSvc.deleteUnusedVolumes();
                return readResponse(response, "Successfully deleted the app.");
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (RuntimeException | IOException | PortainerNotConfigured e) {
            // TODO Improve exception handling
            if (log.isWarnEnabled()) {
                log.warn("Could not process action. [exception=({})]", e.getMessage());
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String deployApp(final App app) throws IOException, PortainerNotConfigured {
        final var data = getService().getDataFromInternalDB((AppImpl) app);
        final var template = IOUtils.toString(data, StandardCharsets.UTF_8);

        // 1. Create Registry with given information from AppStore template.
        final var registryId = portainerSvc.createRegistry(template);

        // 2. Pull Image with given information from AppStore template.
        portainerSvc.pullImage(template);

        // 3. Create volumes with given information from AppStore template.
        final var volumeMap = portainerSvc.createVolumes(template);

        // 4. Create Container with given information from AppStore template and new volume.
        final var containerId = portainerSvc.createContainer(template, volumeMap);

        // Persist containerID.
        getService().setContainerIdForApp(app.getId(), containerId);

        // 5. Get "bride" network-id in Portainer
        final var networkId = portainerSvc.getNetworkId("bridge");

        // 6. Join container into the new created network.
        portainerSvc.joinNetwork(containerId, networkId);

        //7. Delete registry (credentials are one-time-usage)
        portainerSvc.deleteRegistry(registryId);

        return containerId;
    }

    /**
     * @param appId The id of the app.
     * @return Response which describes the current app.
     */
    @GetMapping("/{id}/describe")
    @Operation(summary = "Description of the app container", description = "Can be used for "
            + "describing the current app container.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST,
                    description = ResponseDescription.BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_SERVER_ERROR,
                    description = ResponseDescription.INTERNAL_SERVER_ERROR),
            @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                    description = ResponseDescription.UNAUTHORIZED)})
    @ResponseBody
    public final ResponseEntity<Object> containerDescription(@PathVariable("id") final UUID appId) {
        final var app = getService().get(appId);
        final var containerId = ((AppImpl) app).getContainerId();

        if (containerId == null || containerId.equals("")) {
            return new ResponseEntity<>("No container id provided.", HttpStatus.NOT_FOUND);
        }

        try {
            final var response = portainerSvc.getDescriptionByContainerId(containerId);
            return readResponse(response, response.body());
        } catch (IOException e) {
            // TODO Bad request should only be returned on malformed user input.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get the AppStores related to the given app.
     *
     * @param appId The id of app for which related appstores should be found.
     * @return The app store.
     */
    @GetMapping("/{id}/appstore")
    @Operation(summary = "Get appstore by app id", description = "Get appstore holding this app.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST,
                    description = ResponseDescription.BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_SERVER_ERROR,
                    description = ResponseDescription.INTERNAL_SERVER_ERROR),
            @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                    description = ResponseDescription.UNAUTHORIZED)})
    @ResponseBody
    public final ResponseEntity<Object> relatedAppStore(final @PathVariable("id") UUID appId) {
        return ResponseEntity.ok(getService().getAppStoreByAppId(appId));
    }

    private ResponseEntity<Object> readResponse(final Response response, final Object body) {
        // TODO improve response checking vor portainer service
        if (response != null) {
            final var responseCode = String.valueOf(response.code());

            switch (responseCode) {
                case ResponseCode.NOT_MODIFIED:
                    return new ResponseEntity<>("App is already running.", HttpStatus.BAD_REQUEST);
                case ResponseCode.CONFLICT:
                    return new ResponseEntity<>("Cannot delete a running app.",
                            HttpStatus.BAD_REQUEST);
                case ResponseCode.UNAUTHORIZED:
                    return new ResponseEntity<>("Portainer authorization failed.",
                            HttpStatus.INTERNAL_SERVER_ERROR);
                default:
                    break;
            }

            if (response.isSuccessful()) {
                return ResponseEntity.ok(body);
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
