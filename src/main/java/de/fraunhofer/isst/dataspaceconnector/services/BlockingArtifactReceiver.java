package de.fraunhofer.isst.dataspaceconnector.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class BlockingArtifactReceiver implements ArtifactRetriever {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ArtifactService artifactService;

    @Override
    public InputStream retrieve(final UUID artifactId, final URI recipient, final URI transferContract) {
        final var artifact = artifactService.get(artifactId);
        final var response = messageService.sendArtifactRequestMessage(recipient, artifact.getRemoteAddress(), transferContract);
        final var data = MessageUtils.extractPayloadFromMultipartMessage(response);
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_16));
    }
}
