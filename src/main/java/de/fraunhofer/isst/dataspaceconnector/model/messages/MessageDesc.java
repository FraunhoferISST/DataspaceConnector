package de.fraunhofer.isst.dataspaceconnector.model.messages;

import java.net.URI;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MessageDesc {
    /**
     * The message's recipient.
     */
    private URI recipient;
}
