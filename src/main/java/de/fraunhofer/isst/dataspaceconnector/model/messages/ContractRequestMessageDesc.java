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
public class ContractRequestMessageDesc extends MessageDesc {

    /**
     * The transfer contract of the message.
     */
    private URI transferContract;

    public ContractRequestMessageDesc(final URI recipient, final URI transferContract) {
        super(recipient);

        this.transferContract = transferContract;
    }
}
