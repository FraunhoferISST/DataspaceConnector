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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dataspaceconnector.common.exception.AppNotDeployedException;
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
import io.dataspaceconnector.controller.util.ResponseUtils;
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
import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

/**
 * Offers the endpoints for managing apps.
 */
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
                    description = ResponseDescription.METHOD_NOT_ALLOWED)})
    @Override
    public final ResponseEntity<AppView> create(final AppDesc desc) {
        throw new MethodNotAllowed();
    }

    @Hidden
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                    description = ResponseDescription.METHOD_NOT_ALLOWED)})
    @Override
    public final ResponseEntity<AppView> update(final UUID resourceId, final AppDesc desc) {
        throw new MethodNotAllowed();
    }

    /**
     * @param appId The id of the container.
     * @param type  The action type.
     * @return Response depending on the action on an app.
     */
    @SuppressFBWarnings("IMPROPER_UNICODE")
    @PutMapping("/{id}/actions")
    @Operation(summary = "Actions on apps", description = "Can be used for managing apps.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST,
                    description = ResponseDescription.BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_SERVER_ERROR,
                    description = ResponseDescription.INTERNAL_SERVER_ERROR)})
    @ResponseBody
    public final ResponseEntity<Object> containerManagement(
            @PathVariable("id") final UUID appId,
            @RequestParam("actionType") final String type) {
        final var action = Normalizer.normalize(type.toUpperCase(Locale.ROOT), Normalizer.Form.NFC);
        final var app = getService().get(appId);
        var containerId = ((AppImpl) app).getContainerId();

        Response response;
        try {
            portainerSvc.createEndpointId();

            if (ActionType.START.name().equals(action)) {
                if (containerId == null || containerId.equals("")) {
                    containerId = deployApp(app);
                } else {
                    if (isAppRunning(containerId)) {
                        return new ResponseEntity<>("App is already running.",
                                HttpStatus.BAD_REQUEST);
                    }
                }

                response = portainerSvc.startContainer(containerId);
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
                    return new ResponseEntity<>("No running container found.",
                            HttpStatus.NOT_FOUND);
                }

                if (isAppRunning(containerId)) {
                    return new ResponseEntity<>("Cannot delete a running app.",
                            HttpStatus.BAD_REQUEST);
                }

                response = portainerSvc.deleteContainer(containerId);
                getService().deleteContainerIdFromApp(appId);
                portainerSvc.deleteUnusedVolumes();
                return readResponse(response, "Successfully deleted the app.");
            } else if (ActionType.DESCRIBE.name().equals(action)) {
                if (containerId == null || containerId.equals("")) {
                    return new ResponseEntity<>("No container id provided.", HttpStatus.NOT_FOUND);
                } else {
                    final var descriptionResponse = portainerSvc
                            .getDescriptionByContainerId(containerId);
                    final var responseBody = descriptionResponse.body();

                    if (descriptionResponse.isSuccessful() && responseBody != null) {
                        return ResponseEntity.ok(responseBody.string());
                    } else if (responseBody != null) {
                        return ResponseEntity.internalServerError().body(responseBody.string());
                    } else {
                        return ResponseEntity.internalServerError().body("Response not successful"
                                + " and empty response body!");
                    }
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (PortainerNotConfigured e) {
            return ResponseUtils.respondPortainerNotConfigured(e);
        } catch (AppNotDeployedException e) {
            return ResponseUtils.respondAppNotDeployed(e);
        } catch (RuntimeException | IOException e) {
            return ResponseUtils.respondPortainerError(e);
        }
    }

    private boolean isAppRunning(final String containerID) throws IOException {
        return portainerSvc.validateContainerRunning(containerID);
    }

    private String deployApp(final App app) throws IOException, AppNotDeployedException {
        if (!(app instanceof AppImpl)) {
            //needs to be checked because of cast to AppImpl
            throw new AppNotDeployedException();
        }
        final var data = getService().getDataFromInternalDB((AppImpl) app);
        final var template = IOUtils.toString(data, StandardCharsets.UTF_8);

        // 1. Create Registry with given information from AppStore template.
        final var registryId = portainerSvc.createRegistry(template);

        // 2. Pull Image with given information from AppStore template.
        portainerSvc.pullImage(template);

        // 3. Create volumes with given information from AppStore template.
        final var volumeMap = portainerSvc.createVolumes(
                template, app.getId().toString()
        );

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
     * Get the AppStores related to the given app.
     *
     * @param appId The id of app for which related appstores should be found.
     * @return The app store.
     */
    @GetMapping("/{id}/appstore")
    @Operation(summary = "Get AppStore by app id.", description = "Get appstore holding this app.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST,
                    description = ResponseDescription.BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_SERVER_ERROR,
                    description = ResponseDescription.INTERNAL_SERVER_ERROR)})
    @ResponseBody
    public final ResponseEntity<Object> relatedAppStore(final @PathVariable("id") UUID appId) {
        return ResponseEntity.ok(getService().getAppStoreByAppId(appId));
    }

    private ResponseEntity<Object> readResponse(final Response response, final Object body) {
        if (response != null) {
            final var responseCode = String.valueOf(response.code());

            switch (responseCode) {
                case ResponseCode.NOT_MODIFIED:
                    return new ResponseEntity<>("App is already running.",
                            HttpStatus.BAD_REQUEST);
                case ResponseCode.NOT_FOUND:
                    return new ResponseEntity<>("App not found.",
                            HttpStatus.BAD_REQUEST);
                case ResponseCode.BAD_REQUEST:
                    return new ResponseEntity<>("Error when deleting app.",
                            HttpStatus.INTERNAL_SERVER_ERROR);
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
