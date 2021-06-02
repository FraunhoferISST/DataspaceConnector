package io.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import io.dataspaceconnector.exceptions.NoTransferContractException;
import io.dataspaceconnector.exceptions.PolicyRestrictionException;
import io.dataspaceconnector.services.usagecontrol.ContractManager;
import io.dataspaceconnector.services.usagecontrol.DataProvisionVerifier;
import io.dataspaceconnector.services.usagecontrol.VerificationInput;
import io.dataspaceconnector.services.usagecontrol.VerificationResult;
import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component("PolicyValidator")
@RequiredArgsConstructor
public class PolicyValidator extends IdsValidator<RouteMsg<ArtifactRequestMessageImpl, MessagePayload>> {

    /**
     * Service for contract processing.
     */
    private final @NonNull ContractManager contractManager;

    /**
     * The verifier for the data access.
     */
    private final @NonNull DataProvisionVerifier accessVerifier;

    @Override
    protected void processInternal(final RouteMsg<ArtifactRequestMessageImpl, MessagePayload> msg) throws Exception {
        final var transferContract = MessageUtils.extractTransferContract(msg.getHeader());
        final var requestedArtifact = MessageUtils.extractRequestedArtifact(msg.getHeader());
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());

        if (transferContract == null || transferContract.toString().equals("")) {
            throw new NoTransferContractException("Transfer contract is missing.");
        }

        final var agreement = contractManager.validateTransferContract(
                transferContract, requestedArtifact);
        final var input = new VerificationInput(requestedArtifact, issuer, agreement);
        if (accessVerifier.verify(input) == VerificationResult.DENIED) {
            throw new PolicyRestrictionException(ErrorMessages.POLICY_RESTRICTION);
        }
    }

}
