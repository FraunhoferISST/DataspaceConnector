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
package io.dataspaceconnector.common.net;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.model.artifact.Artifact;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URL;

/**
 * Helper class for managing route references.
 */
@Component
@RequiredArgsConstructor
public final class ApiReferenceHelper {

    /**
     * The default HTTP base URL of the application.
     */
    @Value("${application.http.base-url}")
    private String defaultBaseUrl;

    /**
     * Checks whether a URL is a route reference by checking whether the URL starts with the path
     * to this connector's routes API. The full path to the routes API consists of the application's
     * base URL and the path segment for the routes API.
     *
     * @param url The URL to check.
     * @return True, if the URL is a route reference; false otherwise.
     */
    public boolean isRouteReference(final URL url) {
        final var routesApiUrl = getBaseUrl() + BasePath.ROUTES;
        return url.toString().startsWith(routesApiUrl);
    }

    /**
     * Returns the URI pointing to an artifact's /data endpoint.
     *
     * @param artifact The artifact.
     * @return URI of the /data endpoint.
     */
    public URI getDataUri(final Artifact artifact) {
        final var uri = getBaseUrl()
                .concat(BasePath.ARTIFACTS)
                .concat("/")
                .concat(artifact.getId().toString())
                .concat("/data");
        return URI.create(uri);
    }

    /**
     * Returns the application base URL. When communicating over IDSCP2, no request context is
     * available to get the base URL from, so then a default value is used.
     *
     * @return The base URL.
     */
    private String getBaseUrl() {
        String applicationBaseUrl;

        try {
            applicationBaseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        } catch (IllegalStateException e) {
            //communicating via IDSCPv2, no current request context available
            applicationBaseUrl = defaultBaseUrl;
        }

        return applicationBaseUrl;
    }

}
