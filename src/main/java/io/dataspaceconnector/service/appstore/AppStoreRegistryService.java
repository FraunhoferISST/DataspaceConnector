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
package io.dataspaceconnector.service.appstore;

import de.fraunhofer.ids.messaging.protocol.http.HttpService;
import io.jsonwebtoken.Jwts;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;

/**
 * Service class for app store registries. It allows communicating with Portainer's API to manage
 * Docker Container and Images.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AppStoreRegistryService {

    /**
     * Service for http connections.
     */
    private final @NonNull HttpService httpService;

    /**
     * Service for http connections.
     */
    private final @NonNull AppStoreRegistryConfig appStoreRegistryConfig;

    /**
     * Start index for sub string method.
     */
    private static final int START_INDEX = 8;

    /**
     * Last index for sub string method.
     */
    private static final int LAST_INDEX = 3;

    /**
     * @return If successful, a jwt token is returned for authentication.
     */
    public String authenticate() {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(appStoreRegistryConfig.getDockerHost())
                .port(appStoreRegistryConfig.getDockerPort())
                .addPathSegment("api/auth");
        final var url = urlBuilder.build();
        builder.url(url);
        builder.post(RequestBody.create(createRequestBodyForAuthentication(),
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
     * @return Return response of image request.
     */
    public Response getImages() throws IOException {
        String jwt = getJwtToken();
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(appStoreRegistryConfig.getDockerHost())
                .port(appStoreRegistryConfig.getDockerPort())
                .addPathSegments("api/endpoints/1/docker/images/json")
                .addQueryParameter("all", "0");
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.get();

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     * @return Response of container request.
     */
    public Response getContainers() throws IOException {
        String jwt = getJwtToken();
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(appStoreRegistryConfig.getDockerHost())
                .port(appStoreRegistryConfig.getDockerPort())
                .addPathSegments("api/endpoints/1/docker/containers/json")
                .addQueryParameter("all", "0");
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.get();

        final var request = builder.build();
        return httpService.send(request);
    }

    /**
     *
     * @param imageName The name of the image.
     * @return Response of pulling an image.
     * @throws IOException if an error occurs while pulling the image.
     */
    public Response pullImage(final String imageName) throws IOException {
        String jwt = getJwtToken();
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("http")
                .host(appStoreRegistryConfig.getDockerHost())
                .port(appStoreRegistryConfig.getDockerPort())
                .addPathSegments("api/endpoints/1/docker/images/create")
                .addQueryParameter("fromImage", imageName);
        final var url = urlBuilder.build();
        builder.addHeader("Authorization", "Bearer " + jwt);
        builder.url(url);
        builder.post(RequestBody.create(new byte[0], null));

        final var request = builder.build();
        return httpService.send(request);
    }

    private String createRequestBodyForAuthentication() {
        final var jsonObject = new JSONObject();
        jsonObject.put("Username", appStoreRegistryConfig.getDockerUser());
        jsonObject.put("Password", appStoreRegistryConfig.getDockerPassword());

        return jsonObject.toString();
    }

    private Request.Builder getRequestBuilder() {
        return new Request.Builder();
    }

    private String getJwtToken() {
        String jwtTokenResponse = authenticate();
        return jwtTokenResponse.substring(START_INDEX, jwtTokenResponse.length() - LAST_INDEX);
    }

    private boolean isExpired(final String jwtToken) {
        int i = jwtToken.lastIndexOf('.');
        String withoutSignature = jwtToken.substring(0, i + 1);
        var untrusted = Jwts.parserBuilder().build()
                .parseClaimsJwt(withoutSignature);
        return untrusted.getBody().getExpiration().before(Date.from(Instant.now()));
    }
}
