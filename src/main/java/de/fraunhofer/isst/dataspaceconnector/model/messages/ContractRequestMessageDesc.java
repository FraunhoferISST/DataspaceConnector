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
public class ContractRequestMessageDesc extends MessageDesc {

    /**
     * The transfer contract of the message.
     */
    private URI transferContract;

    /**
     * All args constructor.
     *
     * @param recipient The message's recipient.
     * @param contract  The transfer contract.
     */
    public ContractRequestMessageDesc(final URI recipient, final URI contract) {
        super(recipient);

        this.transferContract = contract;
    }
}
