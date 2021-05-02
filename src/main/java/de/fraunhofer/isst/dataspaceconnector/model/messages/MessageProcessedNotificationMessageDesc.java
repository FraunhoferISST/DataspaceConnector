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
public class MessageProcessedNotificationMessageDesc extends MessageDesc {

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
    public MessageProcessedNotificationMessageDesc(final URI recipient, final URI message) {
        super(recipient);

        this.correlationMessage = message;
    }
}
