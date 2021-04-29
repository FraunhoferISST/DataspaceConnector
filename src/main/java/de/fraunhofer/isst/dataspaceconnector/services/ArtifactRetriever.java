package de.fraunhofer.isst.dataspaceconnector.services;

import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;

/**
 * Performs an artifact request for an artifact.
 */
public interface ArtifactRetriever {
    /**
     * Perform an artifact request for a given artifact.
     * @param artifactId       The artifact whose data should be updated.
     * @param recipient        The target connector holding the artifact's data.
     * @param transferContract The contract authorizing the data transfer.
     * @return The artifact's data.
     */
    InputStream retrieve(UUID artifactId, URI recipient, URI transferContract);

    /**
     * Perform an artifact request for a given artifact with query parameters.
     * @param artifactId       The artifact whose data should be updated.
     * @param recipient        The target connector holding the artifact's data.
     * @param transferContract The contract authorizing the data transfer.
     * @param queryInput       The data query for specifying the requested data.
     * @return The artifact's data.
     */
    InputStream retrieve(UUID artifactId, URI recipient, URI transferContract,
                         QueryInput queryInput);
}
