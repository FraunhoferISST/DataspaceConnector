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
package io.dataspaceconnector.common.net;

import io.dataspaceconnector.common.util.UUIDUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

/**
 * Contains utility methods for processing path and entity IDs.
 */
@Log4j2
public final class EndpointUtils {

    /**
     * Default constructor.
     */
    private EndpointUtils() {
        // not used
    }

    /**
     * Extracts base path and resource id from uri.
     *
     * @param uri The url.
     * @return Endpoint containing base path and resource id (uuid).
     * @throws IllegalArgumentException Failed to extract uuid from string.
     */
    public static EndpointId getEndpointIdFromPath(final URI uri) throws IllegalArgumentException {
        final var fullPath = uri.toString();
        final var allUuids = UUIDUtils.findUuids(fullPath);

        final var resourceId = UUID.fromString(allUuids.get(0));
        final var index = fullPath.lastIndexOf(resourceId.toString()) - 1;
        // -1 so that the / gets also removed
        final var basePath = fullPath.substring(0, index);

        return new EndpointId(basePath, resourceId);
    }

    /**
     * Get current base path as string.
     *
     * @return Base path as string.
     */
    public static String getCurrentBasePathString() {
        final var currentPath = getCurrentBasePath();
        return currentPath.toString().substring(0,
                currentPath.toString().indexOf(currentPath.getPath()));
    }

    /**
     * Determines the current base path from the request context.
     *
     * @return The base path as uri.
     */
    private static URI getCurrentBasePath() {
        return getCurrentRequestUriBuilder().build().toUri();
    }

    /**
     * Builds servlet uri from request context.
     *
     * @return The servlet uri component builder.
     */
    private static ServletUriComponentsBuilder getCurrentRequestUriBuilder() {
        return ServletUriComponentsBuilder.fromCurrentRequest();
    }

    /**
     * Extract uuid from path url.
     *
     * @param url The url.
     * @return The extracted uuid.
     */
    public static UUID getUUIDFromPath(final URI url) {
        try {
            final var endpoint = getEndpointIdFromPath(url);
            return endpoint.getResourceId();
        } catch (IllegalArgumentException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not retrieve uuid from path. [exception=({})]", e.getMessage());
            }
            return null;
        }
    }
}
