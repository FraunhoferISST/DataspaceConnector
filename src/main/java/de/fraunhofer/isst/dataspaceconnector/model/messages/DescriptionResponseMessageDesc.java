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
public class DescriptionResponseMessageDesc extends MessageDesc {

    /**
     * The ids of the correlation message.
     */
    private URI correlationMessage;

    /**
     * All args constructor.
     *
     * @param recipient          The recipient.
     * @param correlationMessage The correlation message.
     */
    public DescriptionResponseMessageDesc(final URI recipient, final URI correlationMessage) {
        super(recipient);
        this.correlationMessage = correlationMessage;
    }
}
