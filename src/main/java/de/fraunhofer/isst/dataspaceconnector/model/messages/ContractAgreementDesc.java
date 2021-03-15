package de.fraunhofer.isst.dataspaceconnector.model.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

/**
 * Class for all description request message parameters.
 */
@Data
@AllArgsConstructor
public class ContractAgreementDesc implements MessageDesc {

    /**
     * The ids of the correlation message.
     */
    private URI correlationMessage;
}
