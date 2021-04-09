package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

public interface RemoteResolver {
    Optional<UUID> identifyByRemoteId(final URI remoteId);
}
