package de.fraunhofer.isst.dataspaceconnector.model.messages;

import java.net.URI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Base class for all messages descriptions.
 */
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MessageDesc {
    /**
     * The message's recipient.
     */
    private URI recipient;
}
