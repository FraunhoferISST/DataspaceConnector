package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.io.InputStream;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.services.BlockingArtifactReceiver;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.SimpleDataAccessVerifier;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimpleArtifactDataGetter {
    @Autowired
    BlockingArtifactReceiver dataReceiver;

    @Autowired
    SimpleDataAccessVerifier accessVerifier;

    @SneakyThrows
    public InputStream getData(final ArtifactService service, final UUID artifactId, final QueryInput queryInput) {
        return service.getData(accessVerifier, dataReceiver, artifactId, queryInput);
    }

    public InputStream getData(final ArtifactService service, final UUID artifactId, final ArtifactService.RetrievalInformation information) {
        return service.getData(accessVerifier, dataReceiver, artifactId, information);
    }
}
