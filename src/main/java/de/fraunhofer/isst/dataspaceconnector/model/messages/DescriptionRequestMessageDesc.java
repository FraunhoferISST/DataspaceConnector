package de.fraunhofer.isst.dataspaceconnector.model.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

/**
 * Class for all description request message parameters.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DescriptionRequestMessageDesc extends MessageDesc {

    /**
     * The requested element of the message.
     */
    private URI requestedElement;

    public DescriptionRequestMessageDesc(final URI recipient, final URI requestedElement) {
        super(recipient);

        this.requestedElement = requestedElement;
    }
}
