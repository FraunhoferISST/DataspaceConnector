package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class EntityDependencyResolver {


    private final @NonNull RelationshipServices.ArtifactRepresentationLinker artifactLinker;

    private final @NonNull RelationshipServices.RepresentationOfferedResourceLinker representationLinker;

    private final @NonNull ArtifactService artifactService;

    public Contract getContractOfferByArtifactId(final URI artifactId) {
        // Get artifact uuid.
        final var endpoint = EndpointUtils.getEndpointIdFromPath(artifactId);
        final var uuid = endpoint.getResourceId();

        // Get artifact and its parents.
        final var artifact = artifactService.get(uuid);

        // Assuming that each entity has only one parent.
        final var representation = getFirstRepresentationByArtifact(artifact);
        final var resource = getFirstOfferedResourceByRepresentation(representation);

        return null;
    }

    /**
     * Get first representation by artifact.
     *
     * @param artifact The artifact.
     * @return The first element of a list of representations.
     */
    private Representation getFirstRepresentationByArtifact(final Artifact artifact) {
        final var representations = artifactLinker.getInternal(artifact);
        return representations.get(0);
    }

    /**
     * Get first resource by representation.
     *
     * @param representation The representations.
     * @return The first element of a list of offered resources.
     */
    private OfferedResource getFirstOfferedResourceByRepresentation(final Representation representation) {
        final var resources = representationLinker.getInternal(representation);
        return resources.get(0);
    }
}
