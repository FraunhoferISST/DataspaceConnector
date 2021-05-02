package de.fraunhofer.isst.dataspaceconnector.model.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.URI;

/**
 * Class for all description request message parameters.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArtifactResponseMessageDesc extends MessageDesc {

    /**
     * The ids of the correlation message.
     */
    private URI correlationMessage;

    /**
     * The transfer contract of the message.
     */
    private URI transferContract;

    /**
     * All args constructor.
     *
     * @param recipient The message's recipient.
     * @param message   The correlation message.
     * @param contract  The transfer contract.
     */
    public ArtifactResponseMessageDesc(final URI recipient, final URI message, final URI contract) {
        super(recipient);

        this.correlationMessage = message;
        this.transferContract = contract;
    }
}
