package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.repositories.RepresentationRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing representations.
 */
@Service
public final class RepresentationService extends BaseEntityService<Representation,
        RepresentationDesc> implements RemoteResolver {

    @Override
    public Optional<UUID> identifyByRemoteId(final URI remoteId) {
        final var repo = (RepresentationRepository) getRepository();
        return repo.identifyByRemoteId(remoteId);
    }
}
