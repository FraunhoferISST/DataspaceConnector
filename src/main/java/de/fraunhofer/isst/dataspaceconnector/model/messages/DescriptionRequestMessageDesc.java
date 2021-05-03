package de.fraunhofer.isst.dataspaceconnector.model.messages;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.URI;

/**
 * Class for all description request message parameters.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DescriptionRequestMessageDesc extends MessageDesc {

    /**
     * The requested element of the message.
     */
    private URI requestedElement;

    /**
     * All args constructor.
     *
     * @param recipient The message's recipient.
     * @param element   The requested element.
     */
    public DescriptionRequestMessageDesc(final URI recipient, final URI element) {
        super(recipient);

        this.requestedElement = element;
    }
}
