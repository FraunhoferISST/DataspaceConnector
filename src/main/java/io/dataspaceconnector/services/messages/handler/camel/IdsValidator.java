package io.dataspaceconnector.services.messages.handler.camel;

import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import io.dataspaceconnector.exceptions.InvalidAffectedResourceException;
import io.dataspaceconnector.exceptions.NoAffectedResourceException;
import io.dataspaceconnector.exceptions.NoRequestedArtifactException;
import io.dataspaceconnector.exceptions.NoTransferContractException;
import io.dataspaceconnector.exceptions.PolicyRestrictionException;
import io.dataspaceconnector.services.messages.types.DescriptionResponseService;
import io.dataspaceconnector.services.usagecontrol.ContractManager;
import io.dataspaceconnector.services.usagecontrol.DataProvisionVerifier;
import io.dataspaceconnector.services.usagecontrol.VerificationInput;
import io.dataspaceconnector.services.usagecontrol.VerificationResult;
import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

public abstract class IdsValidator<I> implements Processor {

    @Override
    @SuppressWarnings("unchecked")
    public void process(final Exchange exchange) throws Exception {
        processInternal((I)exchange.getIn().getBody(Request.class));
    }

    protected abstract void processInternal(I msg) throws Exception;
}

@Log4j2
@Component("CorrectAffectedResourceValidator")
class CorrectAffectedResourceValidator extends IdsValidator<RouteMsg<ResourceUpdateMessageImpl, Resource>> {

    @Override
    protected void processInternal(final RouteMsg<ResourceUpdateMessageImpl, Resource> msg) throws Exception {
        final var affected = MessageUtils.extractAffectedResource(msg.getHeader());
        if (!msg.getBody().getId().equals(affected)) {
            throw new InvalidAffectedResourceException("Resource in message payload does not match affected resource from message header.");
        }
    }

}

@Log4j2
@Component("AffectedResourceValidator")
class AffectedResourceValidator extends IdsValidator<RouteMsg<ResourceUpdateMessageImpl, MessagePayload>> {

    @Override
    protected void processInternal(final RouteMsg<ResourceUpdateMessageImpl, MessagePayload> message) throws Exception {
        final var affected = MessageUtils
                .extractAffectedResource(message.getHeader());

        if (affected == null || affected.toString().isEmpty()) {
            throw new NoAffectedResourceException("Affected resource is null or empty.");
        }
    }

}

@Log4j2
@Component("PolicyValidator")
@RequiredArgsConstructor
class PolicyValidator extends IdsValidator<RouteMsg<ArtifactRequestMessageImpl, MessagePayload>> {

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

@Log4j2
@Component("RequestedArtifactValidator")
class RequestedArtifactValidator extends IdsValidator<RouteMsg<ArtifactRequestMessageImpl, MessagePayload>> {

    @Override
    protected void processInternal(final RouteMsg<ArtifactRequestMessageImpl, MessagePayload> msg) throws Exception {
        final var requestedArtifact = MessageUtils.extractRequestedArtifact(msg.getHeader());
        if (requestedArtifact == null || requestedArtifact.toString().equals("")) {
            throw new NoRequestedArtifactException("Requested artifact is missing.");
        }
    }

}

@Log4j2
@RequiredArgsConstructor
@Component("MessageHeaderValidator")
class MessageHeaderValidator extends IdsValidator<RouteMsg<? extends Message, ?>> {

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    @Override
    protected void processInternal(RouteMsg<? extends Message, ?> msg) throws Exception {
        messageService.validateIncomingMessage(msg.getHeader());
        log.info("Validating and stuff!");
    }
}
