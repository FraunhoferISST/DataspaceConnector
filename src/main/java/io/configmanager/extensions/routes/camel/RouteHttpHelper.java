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
package io.configmanager.extensions.routes.camel;

import io.configmanager.core.OkHttpUtils;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Component for deploying and deleting Camel routes at the Camel application via HTTP.
 */
@Log4j2
@Component
@NoArgsConstructor
public class RouteHttpHelper {
    /**
     * URL of the Camel application.
     */
    @Value("${camel.application.url}")
    private String camelApplicationUrl;

    /**
     * Username for the Camel application.
     */
    @Value("${camel.application.username}")
    private String camelApplicationUsername;

    /**
     * Password for the Camel application.
     */
    @Value("${camel.application.password}")
    private String camelApplicationPassword;

    /**
     * The Camel application's API path for managing routes.
     */
    @Value("${camel.application.path.routes}")
    private String camelApplicationRoutesPath;

    /**
     * The OkHttpClient for sending requests to the Camel application.
     */
    private final OkHttpClient httpClient = OkHttpUtils.getUnsafeOkHttpClient();

    /**
     * Sends an XML route to the Camel application specified in application.properties as a file.
     *
     * @param xml the XML route
     * @throws IOException if the HTTP request cannot be sent or the response status code is not 2xx
     */
    public void sendRouteFileToCamelApplication(final String xml) throws IOException {
        final var url = camelApplicationUrl + camelApplicationRoutesPath;

        final var body = new MultipartBody.Builder().addFormDataPart("file",
                "route.xml", RequestBody.create(xml.getBytes(StandardCharsets.UTF_8),
                        MediaType.parse("application/xml"))).build();

        final var request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", Credentials.basic(camelApplicationUsername,
                        camelApplicationPassword))
                .build();

        try {
            final var response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                if (log.isErrorEnabled()) {
                    log.error("Error sending file to Camel: {}, {}", response.code(),
                            response.body() != null
                                    ? Objects.requireNonNull(response.body()).string()
                                    : "No response body.");
                }

                throw new IOException("Request for deploying route was unsuccessful with code "
                        + response.code());
            }

        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Error sending file to Camel: {}", e.getMessage());
            }

            throw e;
        }
    }

    /**
     * Deletes a route with the given ID at the Camel application specified in
     * application.properties.
     *
     * @param routeId ID of the route to delete
     * @throws IOException if the HTTP request cannot be sent or the response status code is not 2xx
     */
    public void deleteRouteAtCamelApplication(final String routeId) throws IOException {
        final var url = camelApplicationUrl + camelApplicationRoutesPath + "/" + routeId;

        final var request = new Request.Builder()
                .url(url)
                .delete()
                .header("Authorization", Credentials.basic(camelApplicationUsername,
                        camelApplicationPassword))
                .build();

        try {
            final var response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                if (log.isErrorEnabled()) {
                    log.error("Error deleting route at Camel: {}, {}", response.code(),
                            response.body() != null
                                    ? Objects.requireNonNull(response.body()).string()
                                    : "No response body.");
                }

                throw new IOException("Request for deleting route was unsuccessful with code "
                        + response.code());
            }
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Error deleting route at Camel: {}", e.getMessage());
            }

            throw e;
        }
    }
}
