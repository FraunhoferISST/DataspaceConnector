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
package io.dataspaceconnector.camel.processor.controller;

import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ConnectorUnavailableMessageImpl;
import de.fraunhofer.iais.eis.ConnectorUpdateMessageImpl;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementMessageImpl;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.DescriptionRequestMessageImpl;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.QueryLanguage;
import de.fraunhofer.iais.eis.QueryMessageImpl;
import de.fraunhofer.iais.eis.QueryScope;
import de.fraunhofer.iais.eis.QueryTarget;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUnavailableMessageImpl;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.broker.util.FullTextQueryTemplate;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.camel.dto.Request;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.model.message.ArtifactRequestMessageDesc;
import io.dataspaceconnector.model.message.ContractAgreementMessageDesc;
import io.dataspaceconnector.model.message.ContractRequestMessageDesc;
import io.dataspaceconnector.model.message.DescriptionRequestMessageDesc;
import io.dataspaceconnector.service.ids.ConnectorService;
import io.dataspaceconnector.service.message.type.ArtifactRequestService;
import io.dataspaceconnector.service.message.type.ContractAgreementService;
import io.dataspaceconnector.service.message.type.ContractRequestService;
import io.dataspaceconnector.service.message.type.DescriptionRequestService;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
import io.dataspaceconnector.util.QueryInput;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Superclass for all processors that build IDS messages and their payload according to input
 * parameters stored as Exchange properties.
 *
 * @param <H> the type of IDS message.
 * @param <B> the type of payload.
 */
public abstract class IdsMessageBuilder<H extends Message, B> implements Processor {

    /**
     * Override of the {@link Processor}'s process method. Calls the implementing class's
     * processInternal method and sets the result as the {@link Exchange}'s body.
     *
     * @param exchange the exchange.
     * @throws Exception if building the message or payload fails.
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        final var request = processInternal(exchange);
        exchange.getIn().setBody(request);
    }

    /**
     * Creates a request DTO with the desired message type as header and the appropriate payload.
     * To be implemented by sub classes.
     *
     * @param exchange the exchange.
     * @return the {@link Request}
     */
    protected abstract Request<H, B, Optional<Jws<Claims>>> processInternal(Exchange exchange);

}

/**
 * Builds a ContractAgreementMessage and creates a request DTO with header and payload.
 */
@Component("ContractAgreementMessageBuilder")
@RequiredArgsConstructor
class ContractAgreementMessageBuilder extends
        IdsMessageBuilder<ContractAgreementMessageImpl, ContractAgreement> {

    /**
     * The service for managing agreements.
     */
    private final @NonNull ContractAgreementService agreementSvc;

    /**
     * Builds a ContractAgreementMessage and creates a Request with the message as header and
     * the contract agreement from the exchange properties as payload.
     *
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<ContractAgreementMessageImpl, ContractAgreement, Optional<Jws<Claims>>>
    processInternal(final Exchange exchange) {
        final var agreement = exchange
                .getProperty(ParameterUtils.CONTRACT_AGREEMENT_PARAM, ContractAgreement.class);
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);

        final var message = (ContractAgreementMessageImpl) agreementSvc
                .buildMessage(new ContractAgreementMessageDesc(recipient, agreement.getId()));

        return new Request<>(message, agreement, Optional.empty());
    }

}

/**
 * Builds a DescriptionRequestMessage and creates a request DTO with header and payload.
 */
@Component("DescriptionRequestMessageBuilder")
@RequiredArgsConstructor
class DescriptionRequestMessageBuilder extends
        IdsMessageBuilder<DescriptionRequestMessageImpl, String> {

    /**
     * Service for description request message handling.
     */
    private final @NonNull DescriptionRequestService descReqSvc;

    /**
     * Builds a DescriptionRequestMessage according to the exchange properties and creates a Request
     * with the message as header and an empty payload.
     *
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<DescriptionRequestMessageImpl, String, Optional<Jws<Claims>>> processInternal(
            final Exchange exchange) {
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);

        var elementId = exchange.getProperty(ParameterUtils.ELEMENT_ID_PARAM, URI.class);
        if (elementId == null) {
            final var index = exchange.getProperty(Exchange.LOOP_INDEX, Integer.class);
            if (index != null) {
                final var resources = exchange
                        .getProperty(ParameterUtils.RESOURCES_PARAM, List.class);
                elementId = (URI) resources.get(index);
            }
        }
        final var message = (DescriptionRequestMessageImpl) descReqSvc
                .buildMessage(new DescriptionRequestMessageDesc(recipient, elementId));

        return new Request<>(message, "", Optional.empty());
    }
}

/**
 * Builds an ArtifactRequestMessage and creates a request DTO with header and payload.
 */
@Component("ArtifactRequestMessageBuilder")
@RequiredArgsConstructor
class ArtifactRequestMessageBuilder
        extends IdsMessageBuilder<ArtifactRequestMessageImpl, QueryInput> {

    /**
     * Service for artifact request message handling.
     */
    private final @NonNull ArtifactRequestService artifactReqSvc;

    /**
     * Builds an ArtifactRequestMessage according to the exchange properties and creates a Request
     * with the message as header and an empty payload.
     *
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<ArtifactRequestMessageImpl, QueryInput, Optional<Jws<Claims>>>
    processInternal(
            final Exchange exchange) {
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);
        final var agreementId = exchange
                .getProperty(ParameterUtils.TRANSFER_CONTRACT_PARAM, URI.class);
        final var queryInput = exchange
                .getProperty(ParameterUtils.QUERY_INPUT_PARAM, QueryInput.class);

        URI artifactId = exchange.getProperty(ParameterUtils.ARTIFACT_ID_PARAM, URI.class);
        if (artifactId == null) {
            final var index = exchange.getProperty(Exchange.LOOP_INDEX, Integer.class);
            final var artifacts = exchange
                    .getProperty(ParameterUtils.ARTIFACTS_PARAM, List.class);
            artifactId = (URI) artifacts.get(index);
        }

        final var message = (ArtifactRequestMessageImpl) artifactReqSvc
                .buildMessage(new ArtifactRequestMessageDesc(recipient, artifactId, agreementId));

        return new Request<>(message, queryInput, Optional.empty());
    }

}

/**
 * Builds a ContractRequestMessage and creates a request DTO with header and payload.
 */
@Component("ContractRequestMessageBuilder")
@RequiredArgsConstructor
class ContractRequestMessageBuilder
        extends IdsMessageBuilder<ContractRequestMessageImpl, ContractRequest> {

    /**
     * Service for contract processing.
     */
    private final @NonNull ContractManager contractManager;

    /**
     * Service for contract request message handling.
     */
    private final @NonNull ContractRequestService contractReqSvc;

    /**
     * Builds a ContractRequestMessage and a contract request according to the exchange properties
     * and creates a Request with the message as header and the contract request as payload.
     *
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<ContractRequestMessageImpl, ContractRequest, Optional<Jws<Claims>>>
    processInternal(final Exchange exchange) {
        final var recipient = (URI)  exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);
        final var ruleList = exchange.getProperty(ParameterUtils.RULE_LIST_PARAM, List.class);
        final var request = contractManager.buildContractRequest(toRuleList(ruleList));
        exchange.setProperty("contractRequest", request);

        final var message = (ContractRequestMessageImpl) contractReqSvc
                .buildMessage(new ContractRequestMessageDesc(recipient, request.getId()));

        return new Request<>(message, request, Optional.empty());
    }

    @SuppressWarnings("unchecked")
    private static List<Rule> toRuleList(final List<?> list) {
        return (List<Rule>) list;
    }
}

/**
 * Builds a ResourceUpdateMessage and creates a request DTO with header and payload.
 */
@Component("ResourceUpdateMessageBuilder")
@RequiredArgsConstructor
class ResourceUpdateMessageBuilder extends IdsMessageBuilder<ResourceUpdateMessageImpl, Resource> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Builds a ResourceUpdateMessage according to the exchange properties and creates a Request
     * with the message as header and the resource from the exchange body.
     *
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<ResourceUpdateMessageImpl, Resource, Optional<Jws<Claims>>> processInternal(
            final Exchange exchange) {
        final var resource = exchange.getIn().getBody(Resource.class);

        final var connectorId = connectorService.getConnectorId();
        final var modelVersion = connectorService.getOutboundModelVersion();
        final var token = connectorService.getCurrentDat();
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);
        final var resourceId = exchange.getProperty(ParameterUtils.RESOURCE_ID_PARAM, URI.class);

        final var message = new de.fraunhofer.iais.eis.ResourceUpdateMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._affectedResource_(resourceId)
                .build();

        return new Request<>((ResourceUpdateMessageImpl) message, resource, Optional.empty());
    }

}

/**
 * Builds a ResourceUnavailableMessage and creates a request DTO with header and payload.
 */
@Component("ResourceUnavailableMessageBuilder")
@RequiredArgsConstructor
class ResourceUnavailableMessageBuilder
        extends IdsMessageBuilder<ResourceUnavailableMessageImpl, Resource> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Builds a ResourceUnavailableMessage according to the exchange properties and creates a
     * Request with the message as header and the resource from the exchange body.
     *
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<ResourceUnavailableMessageImpl, Resource, Optional<Jws<Claims>>>
    processInternal(final Exchange exchange) {
        final var resource = exchange.getIn().getBody(Resource.class);

        final var connectorId = connectorService.getConnectorId();
        final var modelVersion = connectorService.getOutboundModelVersion();
        final var token = connectorService.getCurrentDat();
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);
        final var resourceId = exchange.getProperty(ParameterUtils.RESOURCE_ID_PARAM, URI.class);

        final var message = new de.fraunhofer.iais.eis.ResourceUnavailableMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._affectedResource_(resourceId)
                .build();

        return new Request<>((ResourceUnavailableMessageImpl) message, resource, Optional.empty());
    }

}

/**
 * Builds a ConnectorUpdateMessage and creates a request DTO with header and payload.
 */
@Component("ConnectorUpdateMessageBuilder")
@RequiredArgsConstructor
class ConnectorUpdateMessageBuilder
        extends IdsMessageBuilder<ConnectorUpdateMessageImpl, Connector> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Builds a ConnectorUpdateMessage according to the exchange properties as well as the connector
     * object and creates a Request with the message as header and the connector as payload.
     *
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<ConnectorUpdateMessageImpl, Connector, Optional<Jws<Claims>>> processInternal(
            final Exchange exchange) {
        final var modelVersion = connectorService.getOutboundModelVersion();
        final var token = connectorService.getCurrentDat();
        final var connector = connectorService.getConnectorWithoutResources();
        final var connectorId = connector.getId();
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);

        final var message = new de.fraunhofer.iais.eis.ConnectorUpdateMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._affectedConnector_(connectorId)
                .build();

        return new Request<>((ConnectorUpdateMessageImpl) message, connector, Optional.empty());
    }

}

/**
 * Builds a ConnectorUnavailableMessage and creates a request DTO with header and payload.
 */
@Component("ConnectorUnavailableMessageBuilder")
@RequiredArgsConstructor
class ConnectorUnavailableMessageBuilder
        extends IdsMessageBuilder<ConnectorUnavailableMessageImpl, Connector> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Builds a ConnectorUnavailableMessage according to the exchange properties as well as the
     * connector object and creates a Request with the message as header and the connector as
     * payload.
     *
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<ConnectorUnavailableMessageImpl, Connector, Optional<Jws<Claims>>>
    processInternal(final Exchange exchange) {
        final var modelVersion = connectorService.getOutboundModelVersion();
        final var token = connectorService.getCurrentDat();
        final var connector = connectorService.getConnectorWithoutResources();
        final var connectorId = connector.getId();
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);

        final var message = new de.fraunhofer.iais.eis.ConnectorUnavailableMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._affectedConnector_(connectorId)
                .build();

        return new Request<>((ConnectorUnavailableMessageImpl) message, connector,
                Optional.empty());
    }

}

/**
 * Builds a QueryMessage and creates a request DTO with header and payload.
 */
@Component("QueryMessageBuilder")
@RequiredArgsConstructor
class QueryMessageBuilder extends IdsMessageBuilder<QueryMessageImpl, String> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Builds a QueryMessage according to the exchange properties and creates a Request with the
     * message as header and a query from the exchange properties as payload.
     *
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<QueryMessageImpl, String, Optional<Jws<Claims>>> processInternal(
            final Exchange exchange) {
        final var modelVersion = connectorService.getOutboundModelVersion();
        final var token = connectorService.getCurrentDat();
        final var connector = connectorService.getConnectorWithoutResources();
        final var connectorId = connector.getId();
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);

        final var message = new de.fraunhofer.iais.eis.QueryMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._queryLanguage_(QueryLanguage.SPARQL)
                ._queryScope_(QueryScope.ALL)
                ._recipientScope_(QueryTarget.BROKER)
                .build();

        String payload;
        if (exchange.getProperty(ParameterUtils.QUERY_PARAM) != null) {
            payload = (String) exchange.getProperty(ParameterUtils.QUERY_PARAM);
        } else {
            final var searchTerm = exchange
                    .getProperty(ParameterUtils.QUERY_TERM_PARAM, String.class);
            final var limit = exchange
                    .getProperty(ParameterUtils.QUERY_LIMIT_PARAM, Integer.class);
            final var offset = exchange
                    .getProperty(ParameterUtils.QUERY_OFFSET_PARAM, Integer.class);

            payload = String.format(FullTextQueryTemplate.FULL_TEXT_QUERY, searchTerm,
                    limit, offset);
        }

        return new Request<>((QueryMessageImpl) message, payload, Optional.empty());
    }

}
