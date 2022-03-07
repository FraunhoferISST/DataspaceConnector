/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
import io.dataspaceconnector.common.net.ContentType;
import io.dataspaceconnector.common.net.JsonResponse;
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
import io.dataspaceconnector.service.AppRouteResolver;
import io.dataspaceconnector.service.appstore.portainer.PortainerService;
import io.dataspaceconnector.service.resource.type.AppEndpointService;
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
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
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
@RestController
@RequiredArgsConstructor
@RequestMapping(BasePath.APPS)
@Tag(name = ResourceName.APPS, description = ResourceDescription.APPS)
public class AppController extends BaseResourceController<App, AppDesc, AppView, AppService> {

    /**
     * Portainer request service.
     */
    private final @NonNull PortainerService portainerSvc;

    /**
     * Service for managing AppEndpoints.
     */
    private final @NonNull AppEndpointService appEndpointSvc;

    /**
     * Service for checking if apps are used by camel routes.
     */
    private final @NonNull AppRouteResolver appRouteResolver;

    /**
     * 443 is the default port for https.
     */
    private static final int DEFAULT_HTTPS_PORT = 443;

    /**
     * The network of the connector to join apps in.
     */
    @Value("${portainer.application.connector.network:local}")
    private String connectorNetwork;

    @Hidden
    @ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
            description = ResponseDescription.METHOD_NOT_ALLOWED)
    @Override
    public final ResponseEntity<AppView> create(final AppDesc desc) {
        throw new MethodNotAllowed();
    }

    @Hidden
    @ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
            description = ResponseDescription.METHOD_NOT_ALLOWED)
    @Override
    public final ResponseEntity<AppView> update(final UUID resourceId, final AppDesc desc) {
        throw new MethodNotAllowed();
    }

    /**
     * Get the AppStores related to the given app.
     *
     * @param appId The id of app for which related appstores should be found.
     * @return The app store.
     */
    @GetMapping(value = "/{id}/appstore", produces = ContentType.HAL)
    @Operation(summary = "Get appstore by app id", description = "Get appstore holding this app.")
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

    /**
     * Perform actions on the apps.
     *
     * @param appId The id of the container.
     * @param type  The action type.
     * @return Response depending on the action on an app.
     */
    @SuppressFBWarnings("IMPROPER_UNICODE")
    @PutMapping(value = "/{id}/actions", produces = ContentType.JSON)
    @Operation(summary = "Actions on apps", description = "Can be used for managing apps.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST,
                    description = ResponseDescription.BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_SERVER_ERROR,
                    description = ResponseDescription.INTERNAL_SERVER_ERROR),
            @ApiResponse(responseCode = ResponseCode.CONFLICT,
                    description = ResponseDescription.CONFLICT),
            @ApiResponse(responseCode = ResponseCode.NOT_FOUND,
                    description = ResponseDescription.NOT_FOUND)})
    @ResponseBody
    public final ResponseEntity<Object> containerManagement(
            @PathVariable("id") final UUID appId,
            @RequestParam("type") final ActionType type) {
        final var app = getService().get(appId);
        final var containerId = ((AppImpl) app).getContainerId();

        try {
            initPortainerSvc();

            if (type == ActionType.START) {
                return startApp(app, containerId);
            } else if (type == ActionType.STOP) {
                return stopApp(app, containerId);
            } else if (type == ActionType.DELETE) {
                return deleteApp(appId, containerId);
            } else if (type == ActionType.DESCRIBE) {
                return describeApp(containerId);
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

    private void initPortainerSvc() throws PortainerNotConfigured, IOException {
        portainerSvc.resetToken();
        portainerSvc.createEndpointId();
    }

    private ResponseEntity<Object> describeApp(final String containerId) throws IOException {
        if (containerId == null || containerId.equals("")) {
            return new JsonResponse("No container id provided.").create(HttpStatus.NOT_FOUND);
        } else {
            final var response = portainerSvc.getDescriptionByContainerId(containerId);
            final var responseBody = response.body();

            if (response.isSuccessful() && responseBody != null) {
                return new JsonResponse(null, null, responseBody.string()).create(HttpStatus.OK);
            } else if (responseBody != null) {
                return new JsonResponse("Response was null.", responseBody.string())
                        .create(HttpStatus.OK);
            } else {
                return new JsonResponse("Response not successful.")
                        .create(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    private ResponseEntity<Object> deleteApp(final @PathVariable("id") UUID appId,
                                             final String containerId) throws IOException {
        Response response;
        if (containerId == null || containerId.equals("")) {
            return new JsonResponse("No running container found.").create(HttpStatus.NOT_FOUND);
        }

        if (isAppRunning(containerId)) {
            return new JsonResponse("Cannot delete a running app.").create(HttpStatus.BAD_REQUEST);
        }

        response = portainerSvc.deleteContainer(containerId);
        getService().deleteContainerIdFromApp(appId);
        portainerSvc.deleteUnusedVolumes();
        return readResponse(response, "Successfully deleted the app.");
    }

    private ResponseEntity<Object> stopApp(final App app,
                                           final String containerId) throws IOException {
        Response response;
        if (containerId == null || containerId.equals("")) {
            return new JsonResponse("No container id provided.").create(HttpStatus.NOT_FOUND);
        }

        final var usedBy = appRouteResolver.isAppUsed(app);
        if (usedBy.isPresent()) {
            return new JsonResponse("Selected App is in use by Camel.",
                    "Camel routes have to be stopped in advance.").create(HttpStatus.CONFLICT);
        }
        response = portainerSvc.stopContainer(containerId);

        return readResponse(response, "Successfully stopped the app.");
    }

    private ResponseEntity<Object> startApp(final App app,
                                            final String containerId)
            throws IOException, AppNotDeployedException {
        String deployedContainerId;

        if (containerId == null || containerId.equals("")) {
            deployedContainerId = deployApp(app);
        } else {
            deployedContainerId = containerId;
            if (isAppRunning(deployedContainerId)) {
                return new JsonResponse("App is already running.").create(HttpStatus.BAD_REQUEST);
            }
        }

        return readResponse(portainerSvc.startContainer(deployedContainerId),
                "Successfully started the app.");
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
        final var volumeMap = portainerSvc.createVolumes(template, app.getId().toString());

        // 4. Create Container with given information from AppStore template and new volume.
        final var containerId = portainerSvc.createContainer(template, volumeMap,
                app.getEndpoints());

        // 5. Get container description from portainer.
        final var containerDesc = portainerSvc.getDescriptionByContainerId(containerId);
        persistContainerData(app, containerId, containerDesc);

        // 6. Get "bride" network-id in Portainer and join app in network
        final var networkIdBridge = portainerSvc.getNetworkId("bridge");
        portainerSvc.joinNetwork(containerId, networkIdBridge);

        // 7. Get setting for connector network and join app in network
        final var networkIdConnector = portainerSvc.getNetworkId(connectorNetwork);
        portainerSvc.joinNetwork(containerId, networkIdConnector);

        // 8. Delete registry (credentials should be one-time-usage)
        portainerSvc.deleteRegistry(registryId);

        return containerId;
    }

    /**
     * Persists the portainer container data, e.g. Container-ID, Container-Name and
     * Endpoint-AccessURLs.
     *
     * @param app           The app currently being deployed.
     * @param containerId   The portainer container id.
     * @param containerDesc The portainer container description.
     * @throws IOException If connection to Portainer threw exception.
     */
    private void persistContainerData(final App app, final String containerId,
                                      final Response containerDesc) throws IOException {
        final var responseBody = containerDesc.body();
        if (responseBody != null) {
            final var name = new JSONObject(responseBody.string()).getString("Name");

            // Note: Portainer places a leading "/" in front of container-name, needs to be removed
            final var containerName = name.substring(name.indexOf("/") + 1);

            // Persist container id and name.
            getService().setContainerName(app.getId(), containerName);
            getService().setContainerIdForApp(app.getId(), containerId);

            // Generate endpoint accessURLs depending on deployment information.
            for (final var endpoint : app.getEndpoints()) {
                final var port = endpoint.getEndpointPort();

                // Uses IDS endpoint description info and not template (/api/apps/{id}/endpoints).
                final var protocol = port == DEFAULT_HTTPS_PORT ? "https://" : "http://";

                // Uses IDS endpoint description info and not template (/api/apps/{id}/endpoints).
                final var suffix =
                        endpoint.getPath() != null ? endpoint.getPath() : "";

                final var location = protocol + containerName + ":" + port + suffix;
                appEndpointSvc.setLocation(endpoint, location);
            }
        }
    }

    private ResponseEntity<Object> readResponse(final Response response, final Object body) {
        if (response != null) {
            if (response.isSuccessful()) {
                return new JsonResponse(body).create(HttpStatus.OK);
            }

            final var responseCode = String.valueOf(response.code());
            switch (responseCode) {
                case ResponseCode.NOT_MODIFIED:
                    return new JsonResponse("App is already running.")
                            .create(HttpStatus.BAD_REQUEST);
                case ResponseCode.NOT_FOUND:
                    return new JsonResponse("App not found.").create(HttpStatus.BAD_REQUEST);
                case ResponseCode.BAD_REQUEST:
                    return new JsonResponse("Error when deleting app.")
                            .create(HttpStatus.INTERNAL_SERVER_ERROR);
                case ResponseCode.CONFLICT:
                    return new JsonResponse("Cannot delete a running app.")
                            .create(HttpStatus.BAD_REQUEST);
                case ResponseCode.UNAUTHORIZED:
                    return new JsonResponse("Portainer authorization failed.")
                            .create(HttpStatus.INTERNAL_SERVER_ERROR);
                default:
                    break;
            }

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
