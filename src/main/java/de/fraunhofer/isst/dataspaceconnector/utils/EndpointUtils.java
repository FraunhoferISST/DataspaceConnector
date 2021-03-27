package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.isst.dataspaceconnector.exceptions.UnreachableLineException;
import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.services.resources.BasePath;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

public final class EndpointUtils {
    private EndpointUtils() {
        // not used
    }

    /**
     * Determines the current endpoint id from the request context.
     *
     * @param resourceId The resource id passed along the request.
     * @return The Endpoint id.
     */
    public static EndpointId getCurrentEndpoint(final UUID resourceId) {
        var basePath = getCurrentRequestUriBuilder().build().toString();

        final var index = basePath.lastIndexOf(resourceId.toString()) - 1;
        // -1 so that the / gets also removed
        basePath = basePath.substring(0, index);

        return new EndpointId(basePath, resourceId);
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
        final var currentPath = EndpointUtils.getCurrentBasePath();
        return currentPath.toString().substring(0,
                currentPath.toString().indexOf(currentPath.getPath()));
    }

    /**
     * Determines the current base path from the request context.
     *
     * @return The base path as uri.
     */
    public static URI getCurrentBasePath() {
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
     * Get base path enum from base path string.
     *
     * @param path The base path as string.
     * @return The type of base path.
     */
    public static BasePath getBasePathEnumFromString(final String path) {
        try {
            return BasePath.fromString(path);
        } catch (UnreachableLineException exception) {
            return null;
        }
    }
}
