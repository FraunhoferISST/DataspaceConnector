package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Resolves an remote address to an local entity.
 */
public interface RemoteResolver {
    /**
     * Search for an local entity by its remote id.
     * @param remoteId The remote id.
     * @return The local entity id.
     */
    Optional<UUID> identifyByRemoteId(URI remoteId);
}
