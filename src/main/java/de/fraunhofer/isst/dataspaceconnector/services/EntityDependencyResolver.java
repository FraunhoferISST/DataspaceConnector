package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handler.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntityDependencyResolver {

    private final @NonNull ResourceService<OfferedResource, ?> resourceService;

    /**
     * Finds resource by a given artifact ID.
     *
     * @param artifactId ID of the artifact
     * @return the resource
     */
    public Optional<Resource> findResourceFromArtifactId(final UUID artifactId) {
//        for (var resource : resourceService.getAll()) {
//            for (var representation : resource.getRepresentation()) {
//                final var representationId = UUIDUtils.uuidFromUri(representation.getId());
//
//                if (representationId.equals(artifactId)) {
//                    return resource;
//                }
//            }
//        }
        return Optional.empty();
    }

    /**
     * Extracts the artifact ID from contract request.
     *
     * @param request the contract
     * @return The artifact ID.
     */
    public URI getArtifactIdFromContract(Contract request) {
        final var obligations = request.getObligation();
        final var permissions = request.getPermission();
        final var prohibitions = request.getProhibition();

        if (obligations != null && !obligations.isEmpty()) return obligations.get(0).getTarget();

        if (permissions != null && !permissions.isEmpty()) return permissions.get(0).getTarget();

        if (prohibitions != null && !prohibitions.isEmpty()) return prohibitions.get(0).getTarget();

        return null;
    }

    /**
     * Gets the contract offer by artifact id.
     *
     * @param artifactId The artifact's id
     * @return The resource's contract offer.
     */
    public ContractOffer getContractOfferByArtifact(UUID artifactId) throws ResourceNotFoundException {
//        final var resource = findResourceFromArtifactId(artifactId);
//        if (resource == null)
//            throw new ResourceNotFoundException("Artifact not known.");
//        return resource.getContractOffer().get(0);

        return null;
    }
}
