package de.fraunhofer.isst.dataspaceconnector.model.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.URI;

/**
 * Class for all description request message parameters.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ContractRejectionMessageDesc extends MessageDesc {

    /**
     * The ids of the correlation message.
     */
    private URI correlationMessage;

    /**
     * All args constructor.
     *
     * @param recipient The message's recipient.
     * @param message   The correlation message.
     */
    public ContractRejectionMessageDesc(final URI recipient, final URI message) {
        super(recipient);

        this.correlationMessage = message;
    }
}
