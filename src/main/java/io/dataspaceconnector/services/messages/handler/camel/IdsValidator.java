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

/**
 * Superclass for Camel processors that validate either header or payload of an incoming message.
 *
 * @param <I> the expected input type (body of the Camel {@link Exchange}).
 */
public abstract class IdsValidator<I> implements Processor {

    /**
     * Override of the the {@link Processor}'s process method. Calls the implementing class's
     * processInternal method.
     *
     * @param exchange the input.
     * @throws Exception if validation fails.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void process(final Exchange exchange) throws Exception {
        processInternal((I)exchange.getIn().getBody(Request.class));
    }

    /**
     * Validates either header of body of the incoming message. To be implemented by sub classes.
     *
     * @param msg the incoming message.
     * @throws Exception if validation fails.
     */
    protected abstract void processInternal(I msg) throws Exception;
}

/**
 * Validates that the correct resource ID was used in a ResourceUpdateMessage.
 */
@Log4j2
@Component("CorrectAffectedResourceValidator")
class CorrectAffectedResourceValidator extends IdsValidator<RouteMsg<ResourceUpdateMessageImpl, Resource>> {

    /**
     * Checks whether the resource ID given in a ResourceUpdateMessage matches the resource ID in
     * the message's payload.
     *
     * @param msg the incoming message.
     * @throws Exception if the IDs do not match.
     */
    @Override
    protected void processInternal(final RouteMsg<ResourceUpdateMessageImpl, Resource> msg) throws Exception {
        final var affected = MessageUtils.extractAffectedResource(msg.getHeader());
        if (!msg.getBody().getId().equals(affected)) {
            throw new InvalidAffectedResourceException("Resource in message payload does not match affected resource from message header.");
        }
    }

}

/**
 * Validates that the resource ID given in the header of a ResourceUpdateMessage is not null or
 * empty.
 */
@Log4j2
@Component("AffectedResourceValidator")
class AffectedResourceValidator extends IdsValidator<RouteMsg<ResourceUpdateMessageImpl, MessagePayload>> {

    /**
     * Checks whether the resource ID given in a ResourceUpdateMessage is null or empty.
     *
     * @param message the incoming message.
     * @throws Exception if the ID is null or empty.
     */
    @Override
    protected void processInternal(final RouteMsg<ResourceUpdateMessageImpl, MessagePayload> message) throws Exception {
        final var affected = MessageUtils
                .extractAffectedResource(message.getHeader());

        if (affected == null || affected.toString().isEmpty()) {
            throw new NoAffectedResourceException("Affected resource is null or empty.");
        }
    }

}

/**
 * Validates the contract used in an ArtifactRequestMessage and checks whether data provision
 * is allowed.
 */
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

    /**
     * First checks whether the contract given in an ArtifactRequestMessage is not null or empty.
     * If it is not, checks whether that contract allows provisioning the data.
     *
     * @param msg the incoming message.
     * @throws Exception if the contract is null or empty or if data provision is denied.
     */
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

/**
 * Validates the requested artifact given in an ArtifactRequestMessage.
 */
@Log4j2
@Component("RequestedArtifactValidator")
class RequestedArtifactValidator extends IdsValidator<RouteMsg<ArtifactRequestMessageImpl, MessagePayload>> {

    /**
     * Checks whether the requested artifact given in an ArtifactRequestMessage is null or empty.
     *
     * @param msg the incoming message.
     * @throws Exception if the requested artifact is null or empty.
     */
    @Override
    protected void processInternal(final RouteMsg<ArtifactRequestMessageImpl, MessagePayload> msg) throws Exception {
        final var requestedArtifact = MessageUtils.extractRequestedArtifact(msg.getHeader());
        if (requestedArtifact == null || requestedArtifact.toString().equals("")) {
            throw new NoRequestedArtifactException("Requested artifact is missing.");
        }
    }

}

/**
 * Validates any incoming message by checking whether the message is empty and whether it references
 * an Infomodel version supported by this connector.
 */
@Log4j2
@RequiredArgsConstructor
@Component("MessageHeaderValidator")
class MessageHeaderValidator extends IdsValidator<RouteMsg<? extends Message, ?>> {

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    /**
     * Checks whether the message is empty and whether it references an Infomodel version supported
     * by this connector.
     *
     * @param msg the incoming message.
     * @throws Exception if the message is empty or references an unsupported Infomodel version.
     */
    @Override
    protected void processInternal(final RouteMsg<? extends Message, ?> msg) throws Exception {
        messageService.validateIncomingMessage(msg.getHeader());
        log.info("Validating and stuff!");
    }
}
