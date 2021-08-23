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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
            return response.body().string();
        } catch (IOException exception) {
            if (log.isWarnEnabled()) {
                log.error(exception.getMessage(), exception);
            }
            return exception.getMessage();
        }
    }

    /**
     * @param containerId The id of the container.
     * @return Response of starting container.
     * @throws IOException if an error occurs while starting the container.
     */
    public Response startContainer(final String containerId) throws IOException {
        String jwt = getJwtToken();
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/containers/" + containerId + "/start");
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.post(RequestBody.create(new byte[0], null));

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * @param containerId The id of the container
     * @return Response of stopping container.
     * @throws IOException if an error occurs while stopping the container.
     */
    public Response stopContainer(final String containerId) throws IOException {
        String jwt = getJwtToken();
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/containers/" + containerId + "/stop");
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.post(RequestBody.create(new byte[0], null));

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * @param containerId The id of the container
     * @return Response of deleting container.
     * @throws IOException if an error occurs while deleting the container.
     */
    public Response deleteContainer(final String containerId) throws IOException {
        String jwt = getJwtToken();
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/containers/" + containerId);
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.delete();

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * @param containerId The id of the container.
     * @return Response is container description.
     * @throws IOException if an error occurs while deleting the container.
     */
    public Response getContainerDescription(final String containerId) throws IOException {
        String jwt = getJwtToken();
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/containers/" + containerId + "/json");
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.get();

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * Gets the Portainer network ID of by its name.
     * @param networkName The name of the portainer network.
     * @return ID of the network in Portainer.
     * @throws IOException Exception while connecting to Portainer.
     */
    public String getNetworkId(final String networkName) throws IOException {
        String jwt = getJwtToken();
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/networks");
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
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

        //If network does not exist, create it.
        return createNetwork(networkName, true, false);
    }

    /**
     * @param appStoreTemplate The template provided by the AppStore decribing 1 App.
     * @return The Id of the created registry.
     * @throws IOException If an error occurs while connection to portainer.
     */
    public Integer createRegistry(final String appStoreTemplate) throws IOException {
        final var templateObject = toJsonObject(appStoreTemplate);
        final var registryURL = templateObject.getString("registry");

        //Check if registry existing (should not be the case but safety check)
        final var registryId = registryExists(registryURL);

        if (registryId != null) {
            //Registry-Credentials from AppStore Template are one time usage only, cant be reused
            deleteRegistry(registryId);
        }

        //Needed registry info from AppStore template for request body:
        //Authentication true/false, Name, Password, Type, URL, Username
        final var requestBody = new JSONObject();
        requestBody.put("URL", registryURL);
        //name of registry will be the url
        requestBody.put("Name", registryURL);
        requestBody.put("Type", REGISTRY_TYPE); //Custom Registry
        requestBody.put("Authentication", false);

        if (templateObject.has("registryUser")) {
            requestBody.put("Authentication", true);
            var authObject = templateObject.getJSONObject("registryUser");
            requestBody.put("Username", authObject.getString("username"));
            requestBody.put("Password", authObject.getString("password"));
        }


        final var jwt = getJwtToken();
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/registries");
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.post(RequestBody.create(requestBody.toString(),
                MediaType.parse("application/json")));

        final var request = builder.build();
        final var response = httpService.send(request);
        final var createdRegistryId = new JSONObject(response.body().string()).get("Id").toString();

        return Integer.parseInt(createdRegistryId);
    }

    /**
     * Deletes a registry by a given registry-id.
     * @param registryId The ID of the registry to be deleted.
     * @throws IOException Exception while connection to portainer.
     */
    public void deleteRegistry(final Integer registryId) throws IOException {
        final var jwt = getJwtToken();
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/registries/" + registryId);

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.delete(RequestBody.create(new byte[0], null));
        final var request = builder.build();
        httpService.send(request);
    }

    /**
     * @param registryURL The new registry url.
     * @return ID of registry if existing.
     * @throws IOException If an error occurs while connection to portainer.
     */
    public Integer registryExists(final String registryURL) throws IOException {
        final var response = getRegistries();
        final var registries = new JSONArray(Objects.requireNonNull(response.body()).string());

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
     * @param force true if disconnect should be forced.
     * @return response from portainer.
     * @throws IOException when request to portainer fails.
     */
    public Response disconnectContainerFromNetwork(
            final String containerID,
            final String networkName,
            final boolean force) throws IOException {
        final var jwt = getJwtToken();
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/networks/" + networkName + "/disconnect");

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
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
        final var jwt = getJwtToken();
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/registries");

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.get();

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * @param imageId id of the image to delete.
     * @return response from portainer.
     * @throws IOException when requesting portainer fails.
     */
    public Response deleteImage(final String imageId) throws IOException {
        final var jwt = getJwtToken();
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/images/" + imageId);

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.delete();

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * @param networkId id of the network to delete.
     * @return response from portainer.
     * @throws IOException when requesting portainer fails.
     */
    public Response deleteNetwork(final String networkId) throws IOException {
        final var jwt = getJwtToken();
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/networks/" + networkId);

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.delete();

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * @param volumeId id of the volume to delete.
     * @return response from portainer.
     * @throws IOException when requesting portainer fails.
     */
    public Response deleteVolume(final String volumeId) throws IOException {
        final var jwt = getJwtToken();
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/volumes/" + volumeId);

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.delete();

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
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
        image = URLEncoder.encode(image, StandardCharsets.UTF_8);
        image += imageTag;

        final var jwt = getJwtToken();
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/images/create")
                .addEncodedQueryParameter("fromImage", registryUrl + "%2F" + image);

        final var url = urlBuilder.build();

        //Initial x-Auth header, Portainer will extend credentials based on registry information
        final var auth = "{\"Username\": \"\", \"Password\": \"\", \"Serveraddress\": \""
                + registryUrl + "\"}";
        final var xAuthHeader = Base64.getEncoder().encodeToString(auth.getBytes());

        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.addHeader("X-Registry-Auth", xAuthHeader);
        builder.url(url);
        builder.post(
                RequestBody.create(
                        new JSONObject()
                                .put("fromImage", registryUrl + "/" + imagePostBody + imageTag)
                                .toString(), MediaType.parse("application/json")));

        final var request = builder.build();
        final var response = httpService.send(request);
        //TODO: Needs to wait until pull image is complete (above will return status updates and not single response)
        return response;
    }

    /**
     * @param appStoreTemplate The template provided by the AppStore describing 1 App.
     * @return Map of portainer responses for every volume to create.
     * @throws IOException If an error occurs while connection to portainer.
     */
    public Map<String, String> createVolumes(final String appStoreTemplate) throws IOException {
        final Map<String, String> volumeNames = new HashMap<>();
        final var templateObject = toJsonObject(appStoreTemplate);
        var volumes = new JSONArray();
        if (!templateObject.isNull("volumes")) {
            volumes = templateObject.getJSONArray("volumes");
        }

        final var jwt = getJwtToken();

        for (int i = 0; i < volumes.length(); i++) {
            final var currentVolume = volumes.getJSONObject(i);
            final var req = new JSONObject();
            final var templateName = currentVolume.getString("container");
            final var validTemplateName = templateName
                    .substring(templateName.indexOf("/") + 1)
                    .replace("/", "_");
            req.put("Name", validTemplateName);
            final var builder = getRequestBuilder();
            final var urlBuilder = new HttpUrl.Builder()
                    .scheme("http")
                    .host(portainerConfig.getPortainerHost())
                    .port(portainerConfig.getPortainerPort())
                    .addPathSegments("api/endpoints/1/docker/volumes/create");
            final var url = urlBuilder.build();
            builder.addHeader("Authorization", "Bearer " + jwt);
            builder.url(url);
            builder.post(
                    RequestBody.create(req.toString(), MediaType.parse("application/json"))
            );

            final var request = builder.build();
            final var response = httpService.send(request);
            volumeNames.put(templateName, new JSONObject(
                    response.body().string()).getString("Name")
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
            var portObj = templateObject.getJSONArray("ports").getJSONObject(i);
            //TODO different field names here?
            var portString = portObj.getString("INPUT_ENDPOINT");
            portString = portString.substring(portString.indexOf(":") + 1);
            ports.add(portString);
        }
        final var jwt = getJwtToken();
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/containers/create");

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);

        //build json payload
        final var jsonPayload = PortainerUtil
                .createContainerJSONPayload(templateObject, ports,  volumes, image);

        //add json payload to request
        builder.post(
                RequestBody.create(jsonPayload.toString(), MediaType.parse("application/json"))
        );

        final var request = builder.build();

        //return container id
        return new JSONObject(httpService.send(request).body().string()).getString("Id");
    }

    /**
     * @param networkName name of the network to create
     * @param pub true if network is public
     * @param adminOnly true if only visible for admins
     * @return Response from portainer
     * @throws IOException if sending request to portainer fails
     */
    public String createNetwork(final String networkName, final boolean pub,
                                  final boolean adminOnly) throws IOException {

        final var jwt = getJwtToken();
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/networks/create");

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
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
        var jsonResp = new JSONObject(response.body().string());
        return jsonResp.getString("Id");
    }

    /**
     * @param containerID Id of the container to add to the network
     * @param networkID networkID to add to
     * @return response from portainer
     * @throws IOException if sending request to portainer fails
     */
    public Response joinNetwork(final String containerID, final String networkID)
            throws IOException {
        final var jwt = getJwtToken();
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments(
                        String.format("api/endpoints/1/docker/networks/%s/connect", networkID)
                );

        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
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
        final var jwt = getJwtToken();
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/" + part);
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
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
