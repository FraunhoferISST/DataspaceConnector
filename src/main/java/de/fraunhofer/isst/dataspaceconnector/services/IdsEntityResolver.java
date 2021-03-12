package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.view.ids.IdsViewer;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdsEntityResolver {

    private final @NonNull IdsViewer idsViewer;

    /**
     * TODO Add catalog
     * @param entity The connector's entity.
     * @return A rdf string of an ids object.
     */
    public String getEntityAsIdsRdfString(final AbstractEntity entity) throws InvalidResourceException {
        String rdf;

        if (entity instanceof Artifact) {
            final var artifact = idsViewer.create((Artifact) entity);
            rdf = IdsUtils.convertArtifactToRdf(artifact);
        } else if (entity instanceof Resource) {
            final var resource = idsViewer.create((Resource) entity);
            rdf = IdsUtils.convertResourceToRdf(resource);
        } else if (entity instanceof Representation) {
            final var representation = idsViewer.create((Representation) entity);
            rdf = IdsUtils.convertRepresentationToRdf(representation);
        } else {
            throw new InvalidResourceException("No provided IDS description for requested element.");
        }

        return rdf;
    }
}
