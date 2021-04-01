package de.fraunhofer.isst.dataspaceconnector.model.messages;

import java.net.URI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class for all artifact request message parameters.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArtifactRequestMessageDesc extends MessageDesc {

    /**
     * The requested artifact of the message.
     */
    private URI requestedArtifact;

    /**
     * The transfer contract of the message.
     */
    private URI transferContract;
}
