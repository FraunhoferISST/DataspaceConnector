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
package io.dataspaceconnector.camel.outgoing;

import java.net.URI;
import java.util.List;

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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

public abstract class IdsMessageBuilder<H extends Message, B> implements Processor {

    @Override
    public void process(final Exchange exchange) throws Exception {
        final var request = processInternal(exchange);
        exchange.getIn().setBody(request);
    }

    protected abstract Request<H, B> processInternal(Exchange exchange);

}

@Component("ContractAgreementMessageBuilder")
@RequiredArgsConstructor
class ContractAgreementMessageBuilder extends
        IdsMessageBuilder<ContractAgreementMessageImpl, ContractAgreement> {

    private final @NonNull ContractAgreementService agreementSvc;

    @Override
    protected Request<ContractAgreementMessageImpl, ContractAgreement> processInternal(final Exchange exchange) {
        final var agreement = exchange
                .getProperty("contractAgreement", ContractAgreement.class);
        final var recipient = exchange.getProperty("recipient", URI.class);

        final var message = (ContractAgreementMessageImpl) agreementSvc
                .buildMessage(new ContractAgreementMessageDesc(recipient, agreement.getId()));

        return new Request<>(message, agreement);
    }

}

@Component("DescriptionRequestMessageBuilder")
@RequiredArgsConstructor
class DescriptionRequestMessageBuilder extends
        IdsMessageBuilder<DescriptionRequestMessageImpl, String> {

    /**
     * Service for description request message handling.
     */
    private final @NonNull DescriptionRequestService descReqSvc;

    @Override
    protected Request<DescriptionRequestMessageImpl, String> processInternal(final Exchange exchange) {
        final var recipient = exchange.getProperty("recipient", URI.class);

        var elementId = exchange.getProperty("elementId", URI.class);
        if (elementId == null) {
            final var index = exchange.getProperty(Exchange.LOOP_INDEX, Integer.class);
            if (index != null) {
                final var resources = (List<URI>) exchange.getProperty("resources", List.class);
                elementId = resources.get(index);
            }
        }
        final var message = (DescriptionRequestMessageImpl) descReqSvc
                .buildMessage(new DescriptionRequestMessageDesc(recipient, elementId));

        return new Request<>(message, "");
    }
}

@Component("ArtifactRequestMessageBuilder")
@RequiredArgsConstructor
class ArtifactRequestMessageBuilder extends IdsMessageBuilder<ArtifactRequestMessageImpl, String> {

    /**
     * Service for artifact request message handling.
     */
    private final @NonNull ArtifactRequestService artifactReqSvc;

    @Override
    protected Request<ArtifactRequestMessageImpl, String> processInternal(final Exchange exchange) {
        final var recipient = exchange.getProperty("recipient", URI.class);
        final var agreementId = exchange.getProperty("agreementId", URI.class);

        URI artifactId = exchange.getProperty("artifactId", URI.class);
        if (artifactId == null) {
            final var index = exchange.getProperty(Exchange.LOOP_INDEX, Integer.class);
            final var artifacts = (List<URI>) exchange.getProperty("artifacts", List.class);
            artifactId = artifacts.get(index);
        }

        final var message = (ArtifactRequestMessageImpl) artifactReqSvc
                .buildMessage(new ArtifactRequestMessageDesc(recipient, artifactId, agreementId));

        return new Request<>(message, "");
    }

}

@Component("ContractRequestMessageBuilder")
@RequiredArgsConstructor
class ContractRequestMessageBuilder extends IdsMessageBuilder<ContractRequestMessageImpl, ContractRequest> {

    /**
     * Service for contract processing.
     */
    private final @NonNull ContractManager contractManager;

    /**
     * Service for contract request message handling.
     */
    private final @NonNull ContractRequestService contractReqSvc;

    @Override
    protected Request<ContractRequestMessageImpl, ContractRequest> processInternal(
            final Exchange exchange) {
        final var ruleList = (List<Rule>) exchange.getProperty("ruleList", List.class);
        final var recipient = exchange.getProperty("recipient", URI.class);

        final var request = contractManager.buildContractRequest(ruleList);
        exchange.setProperty("contractRequest", request);

        final var message = (ContractRequestMessageImpl) contractReqSvc
                .buildMessage(new ContractRequestMessageDesc(recipient, request.getId()));

        return new Request<>(message, request);
    }

}

@Component("ResourceUpdateMessageBuilder")
@RequiredArgsConstructor
class ResourceUpdateMessageBuilder extends IdsMessageBuilder<ResourceUpdateMessageImpl, Resource> {

    private final @NonNull ConnectorService connectorService;

    @Override
    protected Request<ResourceUpdateMessageImpl, Resource> processInternal(final Exchange exchange) {
        final var resource = exchange.getIn().getBody(de.fraunhofer.iais.eis.Resource.class);

        final var connectorId = connectorService.getConnectorId();
        final var modelVersion = connectorService.getOutboundModelVersion();
        final var token = connectorService.getCurrentDat();
        final var recipient = exchange.getProperty("recipient", URI.class);
        final var resourceId = exchange.getProperty("resourceId", URI.class);

        final var message = new de.fraunhofer.iais.eis.ResourceUpdateMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._affectedResource_(resourceId)
                .build();

        return new Request<>((ResourceUpdateMessageImpl) message, resource);
    }

}

@Component("ResourceUnavailableMessageBuilder")
@RequiredArgsConstructor
class ResourceUnavailableMessageBuilder extends IdsMessageBuilder<ResourceUnavailableMessageImpl, Resource> {

    private final @NonNull ConnectorService connectorService;

    @Override
    protected Request<ResourceUnavailableMessageImpl, Resource> processInternal(Exchange exchange) {
        final var resource = exchange.getIn().getBody(de.fraunhofer.iais.eis.Resource.class);

        final var connectorId = connectorService.getConnectorId();
        final var modelVersion = connectorService.getOutboundModelVersion();
        final var token = connectorService.getCurrentDat();
        final var recipient = exchange.getProperty("recipient", URI.class);
        final var resourceId = exchange.getProperty("resourceId", URI.class);

        final var message = new de.fraunhofer.iais.eis.ResourceUnavailableMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._affectedResource_(resourceId)
                .build();

        return new Request<>((ResourceUnavailableMessageImpl) message, resource);
    }

}

@Component("ConnectorUpdateMessageBuilder")
@RequiredArgsConstructor
class ConnectorUpdateMessageBuilder extends IdsMessageBuilder<ConnectorUpdateMessageImpl, Connector> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    @Override
    protected Request<ConnectorUpdateMessageImpl, Connector> processInternal(final Exchange exchange) {
        final var modelVersion = connectorService.getOutboundModelVersion();
        final var token = connectorService.getCurrentDat();
        final var connector = connectorService.getConnectorWithoutResources();
        final var connectorId = connector.getId();
        final var recipient = exchange.getProperty("recipient", URI.class);

        final var message = new de.fraunhofer.iais.eis.ConnectorUpdateMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._affectedConnector_(connectorId)
                .build();

        return new Request<>((ConnectorUpdateMessageImpl) message, connector);
    }

}

@Component("ConnectorUnavailableMessageBuilder")
@RequiredArgsConstructor
class ConnectorUnavailableMessageBuilder extends IdsMessageBuilder<ConnectorUnavailableMessageImpl, Connector> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    @Override
    protected Request<ConnectorUnavailableMessageImpl, Connector> processInternal(final Exchange exchange) {
        final var modelVersion = connectorService.getOutboundModelVersion();
        final var token = connectorService.getCurrentDat();
        final var connector = connectorService.getConnectorWithoutResources();
        final var connectorId = connector.getId();
        final var recipient = exchange.getProperty("recipient", URI.class);

        final var message = new de.fraunhofer.iais.eis.ConnectorUnavailableMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._affectedConnector_(connectorId)
                .build();

        return new Request<>((ConnectorUnavailableMessageImpl) message, connector);
    }

}

@Component("QueryMessageBuilder")
@RequiredArgsConstructor
class QueryMessageBuilder extends IdsMessageBuilder<QueryMessageImpl, String> {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    @Override
    protected Request<QueryMessageImpl, String> processInternal(Exchange exchange) {
        final var modelVersion = connectorService.getOutboundModelVersion();
        final var token = connectorService.getCurrentDat();
        final var connector = connectorService.getConnectorWithoutResources();
        final var connectorId = connector.getId();
        final var recipient = exchange.getProperty("recipient", URI.class);

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
        if (exchange.getProperty("query") != null) {
            payload = (String) exchange.getProperty("query");
        } else {
            final var searchTerm = exchange.getProperty("term", String.class);
            final var limit = exchange.getProperty("limit", Integer.class);
            final var offset = exchange.getProperty("offset", Integer.class);

            payload = String.format(FullTextQueryTemplate.FULL_TEXT_QUERY, searchTerm, limit, offset);
        }

        return new Request<>((QueryMessageImpl) message, payload);
    }

}
