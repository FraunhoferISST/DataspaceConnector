package de.fraunhofer.isst.dataspaceconnector.model.messages;

import lombok.Data;

import java.net.URI;

/**
 * Class for all description request message parameters.
 */
@Data
public class DescriptionRequestDesc implements MessageDesc {
    /**
     * The recipient of the message.
     */
    private URI recipient;

    /**
     * The requested element of the message.
     */
    private URI requestedElement;
}
