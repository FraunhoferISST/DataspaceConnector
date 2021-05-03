package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.ContractAgreement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.URI;

@AllArgsConstructor
@Data
@RequiredArgsConstructor
public class VerificationInput {

    /**
     * The id of the targeted artifact.
     */
    private URI target;

    /**
     * The id of the issuing connector.
     */
    private URI issuerConnector;

    /**
     * The contract agreements for policy verification.
     */
    private ContractAgreement agreement;
}
