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
package io.dataspaceconnector.service.appstore.portainer;

import de.fraunhofer.ids.messaging.protocol.http.HttpService;
import io.dataspaceconnector.common.exception.PortainerNotConfigured;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Service class for app store registries. It allows communicating with Portainer's API to manage
 * Docker Container and Images.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class PortainerRequestService {

    /**
     * Service for http connections.
     */
    private final @NonNull HttpService httpService;

    /**
     * Service for http connections.
     */
    private final @NonNull PortainerConfig portainerConfig;

    /**
     * Start index for sub string method.
     */
    private static final int START_INDEX = 8;

    /**
     * Last index for sub string method.
     */
    private static final int LAST_INDEX = 3;

    /**
     * Registry Type for requests.
     */
    private static final int REGISTRY_TYPE = 3;

    /**
     * The endpoint id in portainer.
     */
    private String endpointId;

    /**
     * Authenticate at portainer.
     *
     * @return If successful, a jwt token is returned for authentication.
     */
    public String authenticate() {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegment("api/auth");

        final var url = urlBuilder.build();
        builder.url(url);
        final var requestBody = createRequestBodyForAuthentication(
                portainerConfig.getPortainerUser(),
                portainerConfig.getPortainerPassword());
        builder.post(RequestBody.create(requestBody,
                MediaType.parse("application/json")));

        final var request = builder.build();
        try {
            final var response = httpService.send(request);
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException exception) {
            if (log.isWarnEnabled()) {
                log.warn(exception.getMessage(), exception);
            }
            return exception.getMessage();
        }
    }

    /**
     * Start container via portainer request.
     *
     * @param containerId The id of the container.
     * @return Response of starting container.
     * @throws IOException if an error occurs while starting the container.
     */
    public Response startContainer(final String containerId) throws IOException {
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/containers/"
                        + containerId + "/start");

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.post(RequestBody.create(new byte[0], null));

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * Stop container via portainer request.
     *
     * @param containerId The id of the container.
     * @return Response of stopping container.
     * @throws IOException if an error occurs while stopping the container.
     */
    public Response stopContainer(final String containerId) throws IOException {
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/containers/"
                        + containerId + "/stop");

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.post(RequestBody.create(new byte[0], null));

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * Delete container via portainer call.
     *
     * @param containerId The id of the container.
     * @return Response of deleting container.
     * @throws IOException if an error occurs while deleting the container.
     */
    public Response deleteContainer(final String containerId) throws IOException {
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId
                        + "/docker/containers/" + containerId);

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.delete();

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * Checks if the container with the given ID is running in Portainer.
     * @param containerId The id of the container.
     * @return Boolean, true if container running, else false.
     * @throws IOException If an error occurs while connecting to Portainer.
     */
    public boolean validateContainerRunning(final String containerId) throws IOException {
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/containers/json")
                .addQueryParameter("all", "1");

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.get();

        final var request = builder.build();
        final var response = httpService.send(request);
        final var containers = new JSONArray(
                Objects.requireNonNull(Objects.requireNonNull(response.body()).string())
        );

        for (final var container : containers) {
            if (((JSONObject) container).get("Id").equals(containerId)) {
                return ((JSONObject) container).get("State").equals("running");
            }
        }

        return false;
    }

    /**
     * Deletes not used images.
     *
     * @throws IOException if an error occurs while deleting not used images.
     */
    public void deleteUnusedImages() throws IOException {

    }

    /**
     * Deletes not used volumes.
     *
     * @throws IOException if an error occurs while deleting not used volumes.
     */
    public void deleteUnusedVolumes() throws IOException {
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/volumes/prune");
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.post(RequestBody.create(new byte[0], null));

        final var request = builder.build();
        httpService.send(request);
    }

    /**
     * Get container description via portainer call.
     *
     * @param containerId The id of the container.
     * @return Response is container description.
     * @throws IOException if an error occurs while deleting the container.
     */
    public Response getDescriptionByContainerId(final String containerId) throws IOException {
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/containers/"
                        + containerId + "/json");

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.get();

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * Get endpoint id.
     *
     * @throws IOException            if an error occurs while requesting the id of the endpoint.
     * @throws PortainerNotConfigured if portainer is not configured.
     */
    public void createEndpointId() throws PortainerNotConfigured, IOException {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints")
                .addQueryParameter("limit", "0")
                .addQueryParameter("start", "1");
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.get();

        final var request = builder.build();
        final var response = httpService.send(request);
        final var jsonArray = new JSONArray(Objects.requireNonNull(response.body()).string());

        for (var tmpObj : jsonArray) {
            if (((JSONObject) tmpObj).getNumber("Type").equals(1)) {
                endpointId = ((JSONObject) tmpObj).get("Id").toString();
            }
        }
        if (endpointId == null) {
            throw new PortainerNotConfigured();
        }
    }

    /**
     * Gets the Portainer network ID of by its name.
     *
     * @param networkName The name of the portainer network.
     * @return ID of the network in Portainer.
     * @throws IOException Exception while connecting to Portainer.
     */
    public String getNetworkId(final String networkName) throws IOException {
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/networks");
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.get();

        final var request = builder.build();
        final var response = httpService.send(request);
        final var networks = new JSONArray(
                Objects.requireNonNull(response.body()).string());

        for (final var network : networks) {
            if (((JSONObject) network).get("Name").equals(networkName)) {
                return ((JSONObject) network).get("Id").toString();
            }
        }

        // If network does not exist, create it.
        return createNetwork(networkName, true, false);
    }

    /**
     * Create registry via portainer request.
     *
     * @param appStoreTemplate The template provided by the AppStore describing 1 App.
     * @return The Id of the created registry.
     * @throws IOException if an error occurs while connection to portainer.
     */
    public Integer createRegistry(final String appStoreTemplate) throws IOException {
        final var templateObject = toJsonObject(appStoreTemplate);
        final var registryURL = templateObject.getString("registry");

        // Check if registry existing (should not be the case but safety check)
        final var registryId = registryExists(registryURL);

        if (registryId != null) {
            // Registry-Credentials from AppStore Template are one time usage only, cant be reused
            deleteRegistry(registryId);
        }

        // Needed registry info from AppStore template for request body:
        // Authentication true/false, Name, Password, Type, URL, Username
        final var requestBody = new JSONObject();
        requestBody.put("URL", registryURL);
        // Name of registry will be the url
        requestBody.put("Name", registryURL);
        requestBody.put("Type", REGISTRY_TYPE); //Custom Registry
        requestBody.put("Authentication", false);

        if (templateObject.has("registryUser")) {
            requestBody.put("Authentication", true);
            var authObject = templateObject.getJSONObject("registryUser");
            requestBody.put("Username", authObject.getString("username"));
            requestBody.put("Password", authObject.getString("password"));
        }

        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/registries");
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.post(RequestBody.create(requestBody.toString(),
                MediaType.parse("application/json")));

        final var request = builder.build();
        final var response = httpService.send(request);
        final var createdRegistryId = new JSONObject(
                Objects.requireNonNull(response.body()).string()
        ).get("Id").toString();

        return Integer.parseInt(createdRegistryId);
    }

    /**
     * Deletes a registry by a given registry-id.
     *
     * @param registryId The ID of the registry to be deleted.
     * @throws IOException Exception while connection to portainer.
     */
    public void deleteRegistry(final Integer registryId) throws IOException {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/registries/" + registryId);

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.delete(RequestBody.create(new byte[0], null));
        final var request = builder.build();
        httpService.send(request);
    }

    /**
     * Return id if registry exists.
     *
     * @param registryURL The new registry url.
     * @return ID of registry if existing.
     * @throws IOException If an error occurs while connection to portainer.
     */
    public Integer registryExists(final String registryURL) throws IOException {
        final var response = getRegistries();
        final var registries = new JSONArray(
                Objects.requireNonNull(Objects.requireNonNull(response.body()).string()));

        for (final var registry : registries) {
            if (((JSONObject) registry).get("URL").equals(registryURL)) {
                return Integer.parseInt(((JSONObject) registry).get("Id").toString());
            }
        }

        return null;
    }

    /**
     * @param containerID id of the container to disconnect.
     * @param networkName name of the network the container should be disconnected from.
     * @param force       true if disconnect should be forced.
     * @return response from portainer.
     * @throws IOException when request to portainer fails.
     */
    public Response disconnectContainerFromNetwork(
            final String containerID,
            final String networkName,
            final boolean force) throws IOException {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/networks/"
                        + networkName + "/disconnect");

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        var jsonPayload = new JSONObject();
        jsonPayload.put("Container", containerID);
        jsonPayload.put("Force", force);
        builder.post(RequestBody.create(
                jsonPayload.toString(),
                MediaType.parse("application/json"))
        );
        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * Get list of all registries.
     *
     * @return response from portainer
     * @throws IOException if request to portainer fails.
     */
    public Response getRegistries() throws IOException {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/registries");

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.get();

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * Delete image.
     *
     * @param imageId id of the image to delete.
     * @return response from portainer.
     * @throws IOException when requesting portainer fails.
     */
    public Response deleteImage(final String imageId) throws IOException {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/images/" + imageId);

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.delete();

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * Delete network by id.
     *
     * @param networkId id of the network to delete.
     * @return response from portainer.
     * @throws IOException when requesting portainer fails.
     */
    public Response deleteNetwork(final String networkId) throws IOException {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/networks/" + networkId);

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.delete();

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * Delete volumes by id.
     *
     * @param volumeId id of the volume to delete.
     * @return response from portainer.
     * @throws IOException when requesting portainer fails.
     */
    public Response deleteVolume(final String volumeId) throws IOException {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/volumes/" + volumeId);

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.delete();

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * Pull image from registry.
     *
     * @param appStoreTemplate The template provided by the AppStore decribing 1 App.
     * @return Response of portainer.
     * @throws IOException If an error occurs while connection to portainer.
     */
    public Response pullImage(final String appStoreTemplate) throws IOException {
        final var templateObject = toJsonObject(appStoreTemplate);
        final var registryUrl = templateObject.getString("registry");

        var image = templateObject.getString("image");
        var imageTag = "";
        if (image.contains(":")) {
            imageTag = ":" + image.split(":")[1];
            image = image.split(":")[0];
        } else {
            imageTag = ":latest";
        }

        final var imagePostBody = image;
        image += imageTag;
        //image = URLEncoder.encode(image, StandardCharsets.UTF_8);

        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/images/create")
                .addQueryParameter("fromImage", registryUrl + "/" + image);
//                .addEncodedQueryParameter("fromImage",
//                        registryUrl + "%2F" + image);

        final var url = urlBuilder.build();

        //Initial x-Auth header, Portainer will extend credentials based on registry information
        final var auth = "{\"Username\": \"\", \"Password\": \"\", \"Serveraddress\": \""
                + registryUrl + "\"}";
        final var xAuthHeader = Base64.getEncoder().encodeToString(auth.getBytes());

        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.addHeader("X-Registry-Auth", xAuthHeader);
        builder.url(url);
        builder.post(
                RequestBody.create(
                        new JSONObject()
                                .put("fromImage", registryUrl + "/" + imagePostBody + imageTag)
                                .toString(), MediaType.parse("application/json")));

        final var request = builder.build();
        final var response = httpService.send(request);

        final var tag = imagePostBody + imageTag;
        waitForImagePull(tag);

        return response;
    }

    private void waitForImagePull(final String tag) throws IOException {
        //Check if image is successfully pulled in Portainer,
        //otherwise wait until process is finished.
        final var maxWaitTimeSec = 60;
        var waitedTimeSec = 0;
        while (true) {
            if (log.isDebugEnabled()) {
                log.debug("Validating image fully pulled (check {}/max {})",
                        waitedTimeSec + 1,
                        maxWaitTimeSec);
            }
            if (checkIfImageExists(tag) || waitedTimeSec == maxWaitTimeSec) {
                break;
            } else {
                try {
                    waitedTimeSec++;
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    break;
                }
            }
        }
    }

    private boolean checkIfImageExists(final String tag) throws IOException {
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/images/json")
                .addQueryParameter("all", "1");

        final var url = urlBuilder.build();

        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.get();

        final var request = builder.build();
        final var response = httpService.send(request);

        return Objects.requireNonNull(response.body()).string().contains(tag);
    }

    /**
     * @param appStoreTemplate The template provided by the AppStore describing 1 App.
     * @param appID UUID of the app volume is created for
     * @return Map of portainer responses for every volume to create.
     * @throws IOException If an error occurs while connection to portainer.
     */
    public Map<String, String> createVolumes(final String appStoreTemplate, final String appID)
            throws IOException {
        final Map<String, String> volumeNames = new HashMap<>();
        final var templateObject = toJsonObject(appStoreTemplate);
        var volumes = new JSONArray();
        if (!templateObject.isNull("volumes")) {
            volumes = templateObject.getJSONArray("volumes");
        }

        for (int i = 0; i < volumes.length(); i++) {
            final var currentVolume = volumes.getJSONObject(i);

            // Get Key Names
            Set<String> names = currentVolume.keySet();
            final var namesList = new ArrayList<>(names);

            final var req = new JSONObject();
            for (var key : namesList) {
                if (!key.equals("container")) {
                    final var value = currentVolume.getString(key) + "_" + appID;
                    req.put(key, value);
                    volumeNames.put(value, key);
                }
            }
            final var templateName = currentVolume.getString("container") + "_" + appID;
            final var validTemplateName = templateName
                    .substring(templateName.indexOf("/") + 1)
                    .replace("/", "_");
            req.put("Name", validTemplateName);

            final var builder = getRequestBuilder();
            final var urlBuilder = new HttpUrl.Builder()
                    .scheme("http")
                    .host(portainerConfig.getPortainerHost())
                    .port(portainerConfig.getPortainerPort())
                    .addPathSegments("api/endpoints/" + endpointId + "/docker/volumes/create");
            final var url = urlBuilder.build();
            builder.addHeader("Authorization", "Bearer " + getJwtToken());
            builder.url(url);
            builder.post(
                    RequestBody.create(req.toString(), MediaType.parse("application/json"))
            );

            final var request = builder.build();
            final var response = httpService.send(request);
            volumeNames.put(templateName, new JSONObject(
                    Objects.requireNonNull(response.body()).string()).getString("Name")
            );
        }
        return volumeNames;
    }

    /**
     * @param appStoreTemplate The template provided by the AppStore describing 1 App.
     * @param volumes          the map for volume names used in the template.
     * @return portainer response.
     * @throws IOException If an error occurs while connection to portainer.
     */
    public String createContainer(final String appStoreTemplate, final Map<String, String> volumes)
            throws IOException {
        final var templateObject = toJsonObject(appStoreTemplate);
        final var image = templateObject.getString("image");
        final List<String> ports = new ArrayList<>();

        //get all ports from the appTemplate
        for (int i = 0; i < templateObject.getJSONArray("ports").length(); i++) {
            var portArr = templateObject.getJSONArray("ports");
            if (!(JSONObject.class.equals(portArr.get(i).getClass()))) {
                if (portArr.get(i).toString().contains(":")) {
                    var defaultPortSpec = portArr.get(i).toString()
                            .substring(portArr.get(i).toString().indexOf(":") + 1);
                    ports.add(defaultPortSpec);
                } else {
                    ports.add(portArr.get(i).toString());
                }
            } else {
                final var keyElements = portArr.getJSONObject(i);
                Set<String> set = keyElements.keySet();
                for (var tmpKey : set) {
                    var port = portArr.getJSONObject(i).getString(tmpKey);
                    port = port.substring(port.indexOf(":") + 1);
                    ports.add(port);
                }
            }
        }

        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/containers/create");

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);

        //build json payload
        final var jsonPayload = PortainerUtil
                .createContainerJSONPayload(templateObject, ports, volumes, image);

        //add json payload to request
        builder.post(
                RequestBody.create(jsonPayload.toString(), MediaType.parse("application/json"))
        );

        final var request = builder.build();

        final var createContainerResponse = httpService.send(request);

        final var body = Objects.requireNonNull(createContainerResponse.body()).string();
        final var portainerObj = new JSONObject(body).getJSONObject("Portainer");
        final var resourceControl = portainerObj.getJSONObject("ResourceControl");
        final var resourceId = resourceControl.getInt("Id");

        updateOwnerShip(resourceId);

        //return container id
        return new JSONObject(body).getString("Id");
    }

    private void updateOwnerShip(final int resourceId) throws IOException {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/resource_controls/" + resourceId);

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);

        var jsonPayload = new JSONObject();
        jsonPayload.put("AdministratorsOnly", true);
        jsonPayload.put("Public", false);
        builder.put(RequestBody.create(jsonPayload.toString(),
                MediaType.parse("application/json")));

        final var request = builder.build();
        httpService.send(request);
    }

    /**
     * @param networkName name of the network to create
     * @param pub         true if network is public
     * @param adminOnly   true if only visible for admins
     * @return Response from portainer
     * @throws IOException if sending request to portainer fails
     */
    private String createNetwork(final String networkName, final boolean pub,
                                 final boolean adminOnly) throws IOException {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/networks/create");

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);

        var jsonPayload = PortainerUtil.createNetworkPayload(
                networkName, pub, adminOnly
        );

        //add json payload to request
        builder.post(
                RequestBody.create(jsonPayload.toString(), MediaType.parse("application/json"))
        );

        final var request = builder.build();
        var response = httpService.send(request);
        var jsonResp = new JSONObject(Objects.requireNonNull(response.body()).string());
        return jsonResp.getString("Id");
    }

    /**
     * @param containerID Id of the container to add to the network
     * @param networkID   networkID to add to
     * @return response from portainer
     * @throws IOException if sending request to portainer fails
     */
    public Response joinNetwork(final String containerID, final String networkID)
            throws IOException {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments(
                        String.format("api/endpoints/" + endpointId
                                + "/docker/networks/%s/connect", networkID)
                );

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);

        var jsonPayload = new JSONObject();
        jsonPayload.put("Container", containerID);

        //add json payload to request
        builder.post(
                RequestBody.create(jsonPayload.toString(), MediaType.parse("application/json"))
        );

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * @return response from portainer
     * @throws IOException if sending request to portainer fails
     */
    public Response getContainers() throws IOException {
        return getItem("containers/json");
    }

    /**
     * @return response from portainer
     * @throws IOException if sending request to portainer fails
     */
    public Response getImages() throws IOException {
        return getItem("images/json");
    }

    /**
     * @return response from portainer
     * @throws IOException if sending request to portainer fails
     */
    public Response getNetworks() throws IOException {
        return getItem("networks");
    }

    /**
     * @return response from portainer
     * @throws IOException if sending request to portainer fails
     */
    public Response getVolumes() throws IOException {
        return getItem("volumes");
    }

    /**
     * @param part api route part depending on requested resource (containers, images...)
     * @return response from portainer
     * @throws IOException if sending request to portainer fails
     */
    public Response getItem(final String part) throws IOException {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/" + endpointId + "/docker/" + part);
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + getJwtToken());
        builder.url(url);
        builder.get();
        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * @return new request builder.
     */
    private Request.Builder getRequestBuilder() {
        return new Request.Builder();
    }

    /**
     * @return auth jwt token for portainer.
     */
    private String getJwtToken() {
        String jwtTokenResponse = authenticate();
        return jwtTokenResponse.substring(START_INDEX, jwtTokenResponse.length() - LAST_INDEX);
    }

    /**
     * @param username The username for the authentication.
     * @param password The password for the authentication.
     * @return request body as string.
     */
    private String createRequestBodyForAuthentication(final String username,
                                                      final String password) {
        final var jsonObject = new JSONObject();
        jsonObject.put("Username", username);
        jsonObject.put("Password", password);

        return jsonObject.toString();
    }

    private JSONObject toJsonObject(final String string) {
        return new JSONObject(string);
    }
}
