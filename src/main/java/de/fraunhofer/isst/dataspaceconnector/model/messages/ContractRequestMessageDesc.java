package de.fraunhofer.isst.dataspaceconnector.model.messages;

import java.net.URI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class for all description request message parameters.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContractRequestMessageDesc extends MessageDesc {

    /**
     * The transfer contract of the message.
     */
    private URI transferContract;
}
