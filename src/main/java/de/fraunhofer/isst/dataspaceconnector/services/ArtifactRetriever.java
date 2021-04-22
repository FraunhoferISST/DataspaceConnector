package de.fraunhofer.isst.dataspaceconnector.services;

import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

public interface ArtifactRetriever {
    InputStream retrieve(UUID artifactId, URI recipient, URI transferContract);
}
