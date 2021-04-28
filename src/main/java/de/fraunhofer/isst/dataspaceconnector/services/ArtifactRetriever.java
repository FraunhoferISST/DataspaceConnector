package de.fraunhofer.isst.dataspaceconnector.services;

import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;

public interface ArtifactRetriever {
    InputStream retrieve(UUID artifactId, URI recipient, URI transferContract);
    InputStream retrieve(UUID artifactId, URI recipient, URI transferContract, QueryInput queryInput);
}
