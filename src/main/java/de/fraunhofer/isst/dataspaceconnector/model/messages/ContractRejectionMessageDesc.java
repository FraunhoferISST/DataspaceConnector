package de.fraunhofer.isst.dataspaceconnector.model.messages;

import java.net.URI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class for all description request message parameters.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class ContractRejectionMessageDesc extends MessageDesc {

    /**
     * The ids of the correlation message.
     */
    private URI correlationMessage;
}
