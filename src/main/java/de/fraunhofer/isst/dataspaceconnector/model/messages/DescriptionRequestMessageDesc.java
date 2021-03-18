package de.fraunhofer.isst.dataspaceconnector.model.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

/**
 * Class for all description request message parameters.
 */
@Data
@AllArgsConstructor
public class DescriptionRequestMessageDesc extends MessageDesc {

    /**
     * The requested element of the message.
     */
    private URI requestedElement;
}
