package de.fraunhofer.isst.dataspaceconnector.model.messages;

import lombok.Data;

import java.net.URI;

@Data
public class MessageDesc {
    /**
     * The message's recipient.
     */
    private URI recipient;
}
