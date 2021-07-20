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
package io.dataspaceconnector.service.util;

import io.dataspaceconnector.exception.UnreachableLineException;

/**
 * The list of the api's paths.
 */
public enum BasePath {

    /**
     * The resource offer's endpoint's base path.
     */
    OFFERS("/api/offers"),

    /**
     * The resource request's endpoint's base path.
     */
    REQUESTS("/api/requests"),

    /**
     * The representation endpoint's base path.
     */
    REPRESENTATIONS("/api/representations"),

    /**
     * The contract endpoint's base path.
     */
    CONTRACTS("/api/contracts"),

    /**
     * The artifact endpoint's base path.
     */
    ARTIFACTS("/api/artifacts"),

    /**
     * The rule endpoint's base path.
     */
    RULES("/api/rules"),

    /**
     * The catalog endpoint's base path.
     */
    CATALOGS("/api/catalogs"),

    /**
     * The contract agreement's base path.
     */
    AGREEMENTS("/api/agreements"),

    /**
     * The routes's base path.
     */
    ROUTES("/api/routes");

    /**
     * The path as string.
     */
    private final String basePath;

    BasePath(final String path) {
        this.basePath = path;
    }

    @Override
    public String toString() {
        final var host = EndpointUtils.getCurrentBasePathString();
        return host + basePath;
    }

    /**
     * Convert string to enum.
     *
     * @param path The base path as string.
     * @return The base path as enum.
     */
    public static BasePath fromString(final String path) {
        for (final var b : BasePath.values()) {
            if (path.contains(b.basePath)) {
                return b;
            }
        }

        throw new UnreachableLineException("This code should not have been reached.");
    }
}
