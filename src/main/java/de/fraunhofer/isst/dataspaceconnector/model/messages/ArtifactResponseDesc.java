package de.fraunhofer.isst.dataspaceconnector.model.messages;

import lombok.Data;

import java.net.URI;

/**
 * Class for all description request message parameters.
 */
@Data
public class ArtifactResponseDesc implements MessageDesc {
    /**
     * The recipient of the message.
     */
    private URI recipient;

    /**
     * The ids of the correlation message.
     */
    private URI correlationMessage;

    /**
     * The transfer contract of the message.
     */
    private URI transferContract;
}
