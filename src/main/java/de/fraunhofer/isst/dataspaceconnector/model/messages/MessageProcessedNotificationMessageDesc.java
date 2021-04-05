package de.fraunhofer.isst.dataspaceconnector.model.messages;

import java.net.URI;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Class for all description request message parameters.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MessageProcessedNotificationMessageDesc extends MessageDesc {

    /**
     * The ids of the correlation message.
     */
    private URI correlationMessage;

    public MessageProcessedNotificationMessageDesc( final URI recipient, final URI correlationMessage ) {
        super(recipient);
        this.correlationMessage = correlationMessage;
    }
}
