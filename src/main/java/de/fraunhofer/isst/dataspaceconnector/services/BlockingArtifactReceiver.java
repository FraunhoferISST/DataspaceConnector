package de.fraunhofer.isst.dataspaceconnector.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import org.jose4j.base64url.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BlockingArtifactReceiver implements ArtifactRetriever {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ArtifactService artifactService;

    @Override
    public InputStream retrieve(final UUID artifactId, final URI recipient, final URI transferContract) {
        return retrieve(artifactId, recipient, transferContract, null);
    }

    @Override
    public InputStream retrieve(final UUID artifactId, final URI recipient, final URI transferContract, final QueryInput queryInput) {
        final var artifact = artifactService.get(artifactId);
        final var response = messageService.sendArtifactRequestMessage(recipient, artifact.getRemoteId(), transferContract, queryInput);
        final var data = MessageUtils.extractPayloadFromMultipartMessage(response);
        return new ByteArrayInputStream(Base64.decode(data));
    }
}
