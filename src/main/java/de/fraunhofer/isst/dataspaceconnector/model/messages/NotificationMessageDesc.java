package de.fraunhofer.isst.dataspaceconnector.model.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

/**
 * Class for all description request message parameters.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationMessageDesc extends MessageDesc {
    /**
     * All args constructor.
     *
     * @param recipient The message's recipient.
     */
    public NotificationMessageDesc(final URI recipient) {
        super(recipient);
    }
}
