package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.isst.dataspaceconnector.exceptions.UnreachableLineException;

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
    AGREEMENTS("/api/agreements");

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
