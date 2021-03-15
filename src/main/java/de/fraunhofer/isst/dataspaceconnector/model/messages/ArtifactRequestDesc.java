package de.fraunhofer.isst.dataspaceconnector.model.messages;

import lombok.Data;

import java.net.URI;

/**
 * Class for all artifact request message parameters.
 */
@Data
public class ArtifactRequestDesc implements MessageDesc {
    /**
     * The recipient of the message.
     */
    private URI recipient;

    /**
     * The requested artifact of the message.
     */
    private URI requestedArtifact;

    /**
     * The transfer contract of the message.
     */
    private URI transferContract;
}
