package io.dataspaceconnector.service.message.handler.validator;

import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import io.dataspaceconnector.common.ErrorMessage;
import io.dataspaceconnector.common.IdsUtils;
import io.dataspaceconnector.common.MessageUtils;
import io.dataspaceconnector.common.exception.PolicyRestrictionException;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.exception.NoTransferContractException;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
import io.dataspaceconnector.service.usagecontrol.DataProvisionVerifier;
import io.dataspaceconnector.service.usagecontrol.ProvisionVerificationInput;
import io.dataspaceconnector.service.usagecontrol.VerificationResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Validates the contract used in an ArtifactRequestMessage and checks whether data provision
 * is allowed.
 */
@Component("PolicyValidator")
@RequiredArgsConstructor
class PolicyValidator extends IdsValidator<Request<ArtifactRequestMessageImpl, MessagePayload,
        Optional<Jws<Claims>>>> {

    /**
     * Service for contract processing.
     */
    private final @NonNull ContractManager contractManager;

    /**
     * The verifier for the data access.
     */
    private final @NonNull DataProvisionVerifier accessVerifier;

    /**
     * First checks whether the contract given in an ArtifactRequestMessage is not null or empty.
     * If it is not, checks whether that contract allows provisioning the data.
     *
     * @param msg the incoming message.
     * @throws Exception if the contract is null or empty or if data provision is denied.
     */
    @Override
    protected void processInternal(final Request<ArtifactRequestMessageImpl, MessagePayload,
            Optional<Jws<Claims>>> msg) throws Exception {
        final var transferContract = MessageUtils.extractTransferContract(msg.getHeader());
        final var requestedArtifact = MessageUtils.extractRequestedArtifact(msg.getHeader());
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());

        if (transferContract == null || transferContract.toString().equals("")) {
            throw new NoTransferContractException("Transfer contract is missing.");
        }

        final var agreement = contractManager.validateTransferContract(
                transferContract, requestedArtifact, issuer);
        final var profile = extractSecurityProfile(msg.getClaims());
        final var input = new ProvisionVerificationInput(requestedArtifact, issuer, agreement,
                profile);
        if (accessVerifier.verify(input) == VerificationResult.DENIED) {
            throw new PolicyRestrictionException(ErrorMessage.POLICY_RESTRICTION);
        }
    }

    private Optional<SecurityProfile> extractSecurityProfile(final Optional<Jws<Claims>> claims) {
        if (claims.isEmpty()) {
            return Optional.empty();
        }

        final var value = claims.get().getBody().get("securityProfile");
        final var profile = IdsUtils.getSecurityProfile(value.toString());
        if (profile.isEmpty()) {
            return Optional.empty();
        }

        return profile;
    }

}
