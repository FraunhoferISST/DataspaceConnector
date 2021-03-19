package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;

public enum BasePath {
    /**
     * The resource endpoint's base path.
     */
    RESOURCES("/api/resources"),

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
    CATALOGS("/api/catalogs");

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
        return null;
    }
}
