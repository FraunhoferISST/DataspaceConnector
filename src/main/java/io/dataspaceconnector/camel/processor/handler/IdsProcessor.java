/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.camel.processor.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementMessageImpl;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.DescriptionRequestMessageImpl;
import de.fraunhofer.iais.eis.NotificationMessageImpl;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RequestMessageImpl;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import io.dataspaceconnector.camel.dto.Request;
import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.dto.RouteMsg;
import io.dataspaceconnector.camel.dto.payload.ContractTargetRuleMapContainer;
import io.dataspaceconnector.camel.exception.AgreementPersistenceException;
import io.dataspaceconnector.camel.exception.UnconfirmedAgreementException;
import io.dataspaceconnector.exception.ContractException;
import io.dataspaceconnector.exception.InvalidInputException;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.message.ArtifactResponseMessageDesc;
import io.dataspaceconnector.model.message.ContractAgreementMessageDesc;
import io.dataspaceconnector.model.message.ContractRejectionMessageDesc;
import io.dataspaceconnector.model.message.DescriptionResponseMessageDesc;
import io.dataspaceconnector.model.message.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.model.subscription.SubscriptionDesc;
import io.dataspaceconnector.service.EntityPersistenceService;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.service.EntityUpdateService;
import io.dataspaceconnector.service.ids.ConnectorService;
import io.dataspaceconnector.service.ids.DeserializationService;
import io.dataspaceconnector.service.message.type.ArtifactResponseService;
import io.dataspaceconnector.service.message.type.ContractAgreementService;
import io.dataspaceconnector.service.message.type.ContractRejectionService;
import io.dataspaceconnector.service.message.type.DescriptionResponseService;
import io.dataspaceconnector.service.message.type.MessageProcessedNotificationService;
import io.dataspaceconnector.service.resource.SubscriptionService;
import io.dataspaceconnector.util.ContractUtils;
import io.dataspaceconnector.util.ErrorMessage;
import io.dataspaceconnector.util.IdsUtils;
import io.dataspaceconnector.util.MessageUtils;
import io.dataspaceconnector.util.QueryInput;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Superclass for Camel processors that execute the final logic to generate a response to an
 * incoming message, e.g. generating a description or providing data.
 *
 * @param <I> the expected input type (body of the Camel {@link Exchange}).
 */
public abstract class IdsProcessor<I extends RouteMsg<?, ?>> implements Processor {

    /**
     * Override of the {@link Processor}'s process method. Calls the implementing class's
     * processInternal method and sets the result as the {@link Exchange}'s body.
     *
     * @param exchange the input.
     * @throws Exception if an error occurs.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void process(final Exchange exchange) throws Exception {
        exchange.getIn().setBody(processInternal((I) exchange.getIn().getBody(Request.class)));
    }

    /**
     * Contains the logic to generate a response to an incoming message.
     *
     * @param msg the incoming message.
     * @return the generated response.
     * @throws Exception if an error occurs.
     */
    protected abstract Response processInternal(I msg) throws Exception;
}

/**
 * Generates a resource description as the response to a DescriptionRequestMessage, if a requested
 * element was given.
 */
@Component("ResourceDescription")
@RequiredArgsConstructor
class ResourceDescriptionProcessor extends IdsProcessor<
        RouteMsg<DescriptionRequestMessageImpl, MessagePayload>> {

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Generates the description of the requested element as the response payload and creates
     * a DescriptionResponseMessage as the response header.
     *
     * @param msg the incoming message.
     * @return a Response object with a DescriptionResponseMessage as header and the resource
     * description as payload.
     * @throws Exception if the resource cannot be found or an error occurs building the response.
     */
    @Override
    protected Response processInternal(final RouteMsg<DescriptionRequestMessageImpl,
            MessagePayload> msg) throws Exception {
        // Read relevant parameters for message processing.
        final var requested = MessageUtils.extractRequestedElement(msg.getHeader());
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var entity = entityResolver.getEntityById(requested);

        if (entity.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessage.EMTPY_ENTITY.toString());
        }

        // If the element has been found, build the ids response message.
        final var desc = new DescriptionResponseMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);
        final var payload = entityResolver.getEntityAsRdfString(entity.get());

        // Send ids response message.
        return new Response(header, payload);
    }
}

/**
 * Generates the connector's self-description as the response to a DescriptionRequestMessage,
 * if no requested element was given.
 */
@Component("SelfDescription")
@RequiredArgsConstructor
class SelfDescriptionProcessor extends IdsProcessor<
        RouteMsg<DescriptionRequestMessageImpl, MessagePayload>> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    /**
     * Generates the self-description as the response payload and creates a
     * DescriptionResponseMessage as the response header.
     *
     * @param msg the incoming message.
     * @return a Response object with a DescriptionResponseMessage as header and the
     * self-description as payload.
     * @throws Exception if an error occurs building the response.
     */
    @Override
    protected Response processInternal(final RouteMsg<DescriptionRequestMessageImpl,
            MessagePayload> msg) throws Exception {
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var connector = connectorService.getConnectorWithOfferedResources();

        // Build ids response message.
        final var desc = new DescriptionResponseMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);

        // Send ids response message.
        return new Response(header, IdsUtils.toRdf(connector));
    }
}

/**
 * Fetches the data of an artifact as the response to an ArtifactRequestMessage.
 */
@Log4j2
@Component("DataRequestProcessor")
@RequiredArgsConstructor
class DataRequestProcessor extends IdsProcessor<
        RouteMsg<ArtifactRequestMessageImpl, MessagePayload>> {

    /**
     * Service for handling artifact response messages.
     */
    private final @NonNull ArtifactResponseService messageService;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Fetches the data of the requested artifact as the response payload and creates an
     * ArtifactResponseMessage as the response header.
     *
     * @param msg the incoming message.
     * @return a Response object with an ArtifactResponseMessage as header and the data as payload.
     * @throws Exception if the {@link QueryInput} given in the request's payload is invalid or
     *                   there is an error fetching the data or an error occurs building the
     *                   response.
     */
    @Override
    protected Response processInternal(final RouteMsg<ArtifactRequestMessageImpl,
            MessagePayload> msg) throws Exception {
        final var artifact = MessageUtils.extractRequestedArtifact(msg.getHeader());
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var transferContract = MessageUtils.extractTransferContract(msg.getHeader());

        final var queryInput = getQueryInputFromPayload(msg.getBody());
        final var data = entityResolver.getDataByArtifactId(artifact, queryInput);

        final var desc = new ArtifactResponseMessageDesc(issuer, messageId, transferContract);
        final var responseHeader = messageService.buildMessage(desc);

        return new Response(responseHeader, Base64Utils.encodeToString(data.readAllBytes()));
    }

    /**
     * Read query parameters from message payload.
     *
     * @param messagePayload The message's payload.
     * @return the query input.
     * @throws InvalidInputException If the query input is not empty but invalid.
     */
    private QueryInput getQueryInputFromPayload(final MessagePayload messagePayload)
            throws InvalidInputException {
        try {
            final var payload = MessageUtils.getStreamAsString(messagePayload);
            if (payload.equals("") || payload.equals("null")) {
                // Query input is optional, so no rejection message will be sent. Query input will
                // be checked for null value in HttpService.class.
                return null;
            } else {
                return new ObjectMapper().readValue(payload, QueryInput.class);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid query input. [exception=({})]", e.getMessage(), e);
            }
            throw new InvalidInputException("Invalid query input.", e);
        }
    }

}

/**
 * Generates the response to a NotificationMessage.
 */
@Component("ProcessedNotification")
@RequiredArgsConstructor
class MessageProcessedProcessor extends IdsProcessor<RouteMsg<NotificationMessageImpl, ?>> {

    /**
     * Service for handling message processed notification messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    /**
     * Creates a MessageProcessedNotificationMessage as the response header.
     *
     * @param msg the incoming message.
     * @return a Response object with a MessageProcessedNotificationMessage as header.
     * @throws Exception if an error occurs building the response.
     */
    @Override
    protected Response processInternal(final RouteMsg<NotificationMessageImpl, ?> msg)
            throws Exception {
        // Build the ids response.
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);

        return new Response(header, "Message processed.");
    }
}

/**
 * Updates a requested resource when a ResourceUpdateMessage is received and generates the response.
 */
@Component("ResourceUpdateProcessor")
@RequiredArgsConstructor
class ResourceUpdateProcessor extends IdsProcessor<RouteMsg<ResourceUpdateMessageImpl, Resource>> {

    /**
     * Service for updating database entities from ids object.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * Service for handling response messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    /**
     * The global event publisher used for handling events.
     */
    private final @NonNull ApplicationEventPublisher publisher;

    /**
     * Updates the local copy of the resource given in the ResourceUpdateMessage and creates a
     * MessageProcessedNotificationMessage as the response header.
     *
     * @param msg the incoming message.
     * @return a Response object with a MessageProcessedNotificationMessage as header.
     * @throws Exception if the resource cannot be updated or an error occurs building the response.
     */
    @Override
    protected Response processInternal(final RouteMsg<ResourceUpdateMessageImpl, Resource> msg)
            throws Exception {
        updateService.updateResource(msg.getBody());

        // Publish the agreement so that the designated event handler sends it to the CH.
        publisher.publishEvent(msg.getBody());

        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());

        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var responseHeader = messageService.buildMessage(desc);

        return new Response(responseHeader, "Message received.");
    }

}

/**
 * Accepts a contract request and generates the response.
 */
@Log4j2
@Component("AcceptContractProcessor")
@RequiredArgsConstructor
class AcceptContractProcessor extends
        IdsProcessor<RouteMsg<ContractRequestMessageImpl, ContractTargetRuleMapContainer>> {

    /**
     * Service for persisting entities.
     */
    private final @NonNull EntityPersistenceService persistenceSvc;

    /**
     * Service for ids contract agreement messages.
     */
    private final @NonNull ContractAgreementService agreementSvc;

    /**
     * Creates a contract agreement from a given contract request and stores the agreement in the
     * database, before generating a response.
     *
     * @param msg the incoming message.
     * @return a Response object with a ContractAgreementMessage as header and the agreement as
     * payload.
     * @throws Exception if the agreement cannot be stores or the response cannot be built.
     */
    @Override
    protected Response processInternal(final RouteMsg<ContractRequestMessageImpl,
            ContractTargetRuleMapContainer> msg) throws Exception {
        final var targets = new ArrayList<>(msg.getBody().getTargetRuleMap().keySet());
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());

        // Turn the accepted contract request into a contract agreement and persist it.
        final ContractAgreement agreement;
        try {
            agreement = persistenceSvc.buildAndSaveContractAgreement(
                    msg.getBody().getContractRequest(), targets, issuer);
        } catch (ConstraintViolationException | PersistenceException exception) {
            throw new AgreementPersistenceException("Failed to build or persist agreement.",
                    exception);
        }

        // Build ids response message.
        final var desc = new ContractAgreementMessageDesc(issuer, messageId);
        final var header = agreementSvc.buildMessage(desc);
        if (log.isDebugEnabled()) {
            log.debug("Contract request accepted. [agreementId=({})]", agreement.getId());
        }

        // Send ids response message.
        return new Response(header, IdsUtils.toRdf(agreement));
    }

}

/**
 * Rejects a contract request and generates the response.
 */
@Component("RejectContractProcessor")
@RequiredArgsConstructor
class RejectContractProcessor extends
        IdsProcessor<RouteMsg<ContractRequestMessageImpl, ContractTargetRuleMapContainer>> {

    /**
     * Service for ids contract rejection messages.
     */
    private final @NonNull ContractRejectionService rejectionService;

    /**
     * Generates the response for rejecting a contract.
     *
     * @param msg the incoming message.
     * @return a Response object with a ContractRejectionMessage as header.
     * @throws Exception if the response cannot be built.
     */
    @Override
    protected Response processInternal(final RouteMsg<ContractRequestMessageImpl,
            ContractTargetRuleMapContainer> msg) throws Exception {
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());

        // Build ids response message.
        final var desc = new ContractRejectionMessageDesc(issuer, messageId);
        final var header = (RejectionMessage) rejectionService.buildMessage(desc);

        // Send ids response message.
        return new Response(header, "Contract rejected.");
    }

}

/**
 * Compares the contract agreement given in a ContractAgreementMessage to the locally stored
 * agreement and generates the response.
 */
@Component("AgreementComparisonProcessor")
@RequiredArgsConstructor
class AgreementComparisonProcessor extends IdsProcessor<
        RouteMsg<ContractAgreementMessageImpl, ContractAgreement>> {

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for updating database entities from ids object.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * The global event publisher used for handling events.
     */
    private final @NonNull ApplicationEventPublisher publisher;

    /**
     * Service for handling notification messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    /**
     * Compares the contract agreement given by the consumer to the one stored in the database
     * and saves it as confirmed if they match.
     *
     * @param msg the incoming message.
     * @return a Response object with a MessageProcessedNotificationMessage as header.
     * @throws Exception if the contracts do not match or the confirmed agreement cannot be stored.
     */
    @Override
    protected Response processInternal(
            final RouteMsg<ContractAgreementMessageImpl, ContractAgreement> msg) throws Exception {
        final var agreement = msg.getBody();
        final var entity = entityResolver.getEntityById(agreement.getId());
        if (entity.isEmpty()) {
            throw new ResourceNotFoundException(ErrorMessage.EMTPY_ENTITY.toString());
        }
        final var storedAgreement = (Agreement) entity.get();
        final var storedIdsAgreement =
                deserializationService.getContractAgreement(storedAgreement.getValue());

        if (!ContractUtils.compareContractAgreements(agreement, storedIdsAgreement)) {
            throw new ContractException("Received agreement does not match stored agreement.");
        }

        if (!updateService.confirmAgreement(storedAgreement)) {
            throw new UnconfirmedAgreementException(storedAgreement,
                    "Could not confirm agreement.");
        }

        // Publish the agreement so that the designated event handler sends it to the CH.
        publisher.publishEvent(agreement);

        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());

        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var responseHeader = messageService.buildMessage(desc);

        return new Response(responseHeader, "Received contract agreement message.");
    }

}

/**
 * Generates the response to a SubscriptionMessage.
 */
@Component("ProcessedSubscription")
@Log4j2
@RequiredArgsConstructor
class SubscriptionProcessor extends IdsProcessor<RouteMsg<RequestMessageImpl, ?>> {

    /**
     * Service for handling message processed subscription messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    /**
     * Handler for adding and removing subscriptions.
     */
    private final @NonNull SubscriptionService subscriptionSvc;

    /**
     * Creates a MessageProcessedNotificationMessage as the response header.
     *
     * @param msg the incoming message.
     * @return a Response object with a MessageProcessedNotificationMessage as header.
     * @throws Exception if an error occurs building the response.
     */
    @Override
    protected Response processInternal(final RouteMsg<RequestMessageImpl, ?> msg) throws Exception {
        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var target = MessageUtils.extractTargetId(msg.getHeader());
        final var optional = getSubscriptionFromPayload((MessagePayload) msg.getBody());

        String response;
        if (optional.isPresent()) {
            final var subscription = optional.get();

            // Create new subscription.
            final var desc = new SubscriptionDesc();
            desc.setSubscriber(issuer);
            desc.setTarget(subscription.getTarget());
            desc.setPushData(subscription.isPushData());
            desc.setLocation(subscription.getLocation());

            // Set boolean to true as this subscription has been created via ids message.
            desc.setIdsProtocol(true);

            // Create subscription (this will also be linked to the target)
            subscriptionSvc.create(desc);

            response = "Successfully subscribed to %s.";
        } else {
            subscriptionSvc.removeSubscription(target, issuer);
            response = "Successfully unsubscribed from %s.";
        }

        // Build the ids response.
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());
        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);

        return new Response(header, String.format(response, target));
    }

    /**
     * Read subscription from message payload.
     *
     * @param messagePayload The message's payload.
     * @return the subscription input.
     * @throws InvalidInputException if the subscription is not empty but invalid.
     */
    private Optional<Subscription> getSubscriptionFromPayload(final MessagePayload messagePayload)
            throws InvalidInputException {
        try {
            final var payload = MessageUtils.getStreamAsString(messagePayload);
            if (payload.equals("") || payload.equals("null")) {
                return Optional.empty();
            } else {
                final var subscription = new ObjectMapper().readValue(payload, Subscription.class);
                return Optional.of(subscription);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Invalid subscription payload. [exception=({})]", e.getMessage(), e);
            }
            throw new InvalidInputException("Invalid subscription payload.", e);
        }
    }
}
