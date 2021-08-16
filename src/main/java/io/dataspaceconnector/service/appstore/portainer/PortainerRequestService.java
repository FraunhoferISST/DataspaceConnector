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
import net.minidev.json.JSONArray;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.SocketUtils;

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

        //Needed registry info from AppStore template for request body:
        //Authentication true/false, Name, Password, Type, URL, Username
        final var requestBody = new JSONObject();
        requestBody.put("url", templateObject.getString("registry"));
        //name of registry will be the url
        requestBody.put("name", templateObject.getString("registry"));
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
        builder.post(RequestBody.create(requestBody.toString(), null));

        final var request = builder.build();
        final var response = httpService.send(request);
        return response;
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
        );

        //Needed info from AppStore template for URL params:
        //Registry-URL and Image-Details

        final var jwt = getJwtToken();
        final var builder = getRequestBuilder();

        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(portainerConfig.getPortainerHost())
                .port(portainerConfig.getPortainerPort())
                .addPathSegments("api/endpoints/1/docker/images/create")
                .addQueryParameter("fromImage", templateObject.getString("registry")
                        + "/" + templateObject.getString("image"));
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.post(
                RequestBody.create(
                        new JSONObject()
                                .put("fromImage", templateObject.getString("registry")
                                        + "/" + templateObject.getString("image"))
                                .toString(),
                        null));

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
        final var volumes = templateObject.getJSONArray("volumes");

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
        final var jsonPayload = new JSONObject();

        //fill single fields of json payload
        jsonPayload.put("Env", new JSONArray());
        jsonPayload.put("OpenStdin", false);
        jsonPayload.put("Tty", false);
        jsonPayload.put("Labels", new JSONObject());
        jsonPayload.put("name", "");
        jsonPayload.put("Cmd", new JSONArray());
        jsonPayload.put("Image", templateObject.getString("registry")
                + "/" + templateObject.getString("image"));

        //build exposed ports part of json payload
        final var exposedPorts = new JSONObject();
        for (var port : ports) {
            exposedPorts.put(port, new JSONObject());
        }
        jsonPayload.put("ExposedPorts", exposedPorts);

        //build hostConfig part of json payload
        final var hostConfig = new JSONObject();
        hostConfig.put("Privileged", false);
        hostConfig.put("ExtraHosts", new JSONArray());
        hostConfig.put("NetworkMode", "bridge");
        final String restartPolicy;
        if (templateObject.has("restart_policy")) {
            restartPolicy = templateObject.getString("restart_policy");
        } else {
            restartPolicy = "always";
        }
        hostConfig.put("RestartPolicy", new JSONObject(
                String.format("{\"Name\":\"%s\"}", restartPolicy))
        );
        final var portBindings = new JSONObject();
        for (var port : ports) {
            portBindings.put(port, new JSONArray()
                    .appendElement(new JSONObject()
                            .put("HostPort", String.valueOf(SocketUtils.findAvailableTcpPort()))));
        }
        hostConfig.put("PortBindings", portBindings);
        final var binds = new JSONArray();
        for (var bind : volumes.entrySet()) {
            binds.appendElement(bind.getValue() + ":" + bind.getKey());
        }
        hostConfig.put("Binds", binds);
        jsonPayload.put("HostConfig", hostConfig);

        jsonPayload.put("name", image);

        //build volumes part of json payload
        final var volumesJSON = new JSONObject();
        for (var bind : volumes.entrySet()) {
            volumesJSON.put(bind.getKey(), new JSONObject());
        }
        jsonPayload.put("Volumes", volumesJSON);

        //add json payload to request
        builder.post(
                RequestBody.create(jsonPayload.toString(), MediaType.parse("application/json"))
        );

        final var request = builder.build();

        //return container id
        return new JSONObject(httpService.send(request).body().string()).getString("Id");
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
