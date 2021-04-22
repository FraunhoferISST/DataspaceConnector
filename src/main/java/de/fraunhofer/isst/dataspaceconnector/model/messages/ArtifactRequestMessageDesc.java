package de.fraunhofer.isst.dataspaceconnector.model.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.URI;

/**
 * Class for all artifact request message parameters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    /**
     * All args constructor.
     *
     * @param recipient         The recipient.
     * @param requestedArtifact The requested artifact.
     * @param transferContract  The transfer contract.
     */
    public ArtifactRequestMessageDesc(final URI recipient, final URI requestedArtifact,
                                      final URI transferContract) {
        super(recipient);

        this.requestedArtifact = requestedArtifact;
        this.transferContract = transferContract;
    }
}
