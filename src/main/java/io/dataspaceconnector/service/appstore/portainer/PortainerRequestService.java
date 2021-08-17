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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param appStoreTemplate The template provided by the AppStore decribing 1 App.
     * @return Response of portainer.
     * @throws IOException If an error occurs while connection to portainer.
     */
    public Response createRegistry(final String appStoreTemplate) throws IOException {
        final var templateObject = toJsonObject(appStoreTemplate);
        final var registryURL = templateObject.getString("registry");

        if (registryExists(registryURL)) return null;

        //Needed registry info from AppStore template for request body:
        //Authentication true/false, Name, Password, Type, URL, Username
        final var requestBody = new JSONObject();
        requestBody.put("url", registryURL);
        //name of registry will be the url
        requestBody.put("name", registryURL);
        requestBody.put("type", REGISTRY_TYPE); //Custom Registry
        requestBody.put("authentication", false);

        if (templateObject.has("username") && templateObject.has("password")) {
            requestBody.put("authentication", true);
            //TODO: Where does AppStore template provide credentials?
            requestBody.put("username", templateObject.getString("username"));
            requestBody.put("password", templateObject.getString("password"));
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
        return response;
    }

    public boolean registryExists(final String registryURL) throws IOException {
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
        var response = httpService.send(request);
        return response.body().string().contains(registryURL);
    }

    /**
     * @param appStoreTemplate The template provided by the AppStore decribing 1 App.
     * @return Response of portainer.
     * @throws IOException If an error occurs while connection to portainer.
     */
    public Response pullImage(final String appStoreTemplate) throws IOException {
        final var templateObject = toJsonObject(appStoreTemplate);
        final var registryUrl = URLEncoder.encode(
                templateObject.getString("registry"), StandardCharsets.UTF_8
        );
        final var image = URLEncoder.encode(
                templateObject.getString("image"), StandardCharsets.UTF_8
        ).replace("%3A", ":");
        final var encodedSlash = URLEncoder.encode("/", StandardCharsets.UTF_8);

        //Needed info from AppStore template for URL params:
        //Registry-URL and Image-Details

        final var jwt = getJwtToken();
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/images/create")
                .addEncodedQueryParameter("fromImage", registryUrl
                        + encodedSlash + image);
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.post(
                RequestBody.create(
                        new JSONObject()
                                .put("fromImage", templateObject.getString("registry")
                                        + "/" + templateObject.getString("image"))
                                .toString(), MediaType.parse("application/json")));

        final var request = builder.build();
        return httpService.send(request);
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
            var portString = templateObject.getJSONArray("ports").getString(i);
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

    private Request.Builder getRequestBuilder() {
        return new Request.Builder();
    }

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
