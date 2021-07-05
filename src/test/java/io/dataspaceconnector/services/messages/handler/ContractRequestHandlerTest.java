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
package io.dataspaceconnector.services.messages.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractAgreementMessage;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.ContractRequestMessageBuilder;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.contract.ContractDesc;
import io.dataspaceconnector.model.contract.ContractFactory;
import io.dataspaceconnector.model.rule.ContractRuleDesc;
import io.dataspaceconnector.model.rule.ContractRuleFactory;
import io.dataspaceconnector.services.EntityPersistenceService;
import io.dataspaceconnector.services.ids.ConnectorService;
import io.dataspaceconnector.services.messages.types.ContractAgreementService;
import io.dataspaceconnector.services.resources.EntityDependencyResolver;
import de.fraunhofer.ids.messaging.handler.message.MessagePayloadInputstream;
import de.fraunhofer.ids.messaging.response.BodyResponse;
import de.fraunhofer.ids.messaging.response.ErrorResponse;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ContractRequestHandlerTest {

    @MockBean
    private ConnectorService connectorService;

    @SpyBean
    EntityPersistenceService persistenceService;

    @SpyBean
    EntityDependencyResolver dependencyResolver;

    @SpyBean
    ContractAgreementService agreementService;

    @Autowired
    ContractRequestHandler handler;

    private final String version = "4.0.0";
    private final URI uri = URI.create("https://localhost:8080");
    private final DynamicAttributeToken token = new DynamicAttributeTokenBuilder()
            ._tokenValue_("token")
            ._tokenFormat_(TokenFormat.JWT)
            .build();

    @Test
    public void handleMessage_nullMessage_returnBadParametersResponse() {
        /* ARRANGE */
        final var payload = new MessagePayloadInputstream(InputStream.nullInputStream(),
                new ObjectMapper());

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage(null, payload);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS,
                result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_unsupportedMessage_returnUnsupportedVersionRejectionMessage() {
        /* ARRANGE */
        final var xmlCalendar = getCalender();
        final var message = new ContractRequestMessageBuilder()
                ._senderAgent_(uri)
                ._issuerConnector_(uri)
                ._securityToken_(token)
                ._modelVersion_("tetris")
                ._issued_(xmlCalendar)
                .build();

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage(
                (ContractRequestMessageImpl) message, null);

        /* ASSERT */
        assertEquals(RejectionReason.VERSION_NOT_SUPPORTED,
                result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_nullPayload_returnBadRequestErrorResponse() {
        /* ARRANGE */
        final var xmlCalendar = getCalender();
        final var message = new ContractRequestMessageBuilder()
                ._senderAgent_(uri)
                ._issuerConnector_(uri)
                ._securityToken_(token)
                ._modelVersion_(version)
                ._issued_(xmlCalendar)
                .build();

        Mockito.doNothing().when(agreementService).validateIncomingMessage(message);
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage(
                (ContractRequestMessageImpl) message, null);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS,
                result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_emptyPayload_returnBadRequestErrorResponse() {
        /* ARRANGE */
        final var xmlCalendar = getCalender();
        final var message = new ContractRequestMessageBuilder()
                ._senderAgent_(uri)
                ._issuerConnector_(uri)
                ._securityToken_(token)
                ._modelVersion_(version)
                ._issued_(xmlCalendar)
                .build();

        Mockito.doNothing().when(agreementService).validateIncomingMessage(message);
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage(
                (ContractRequestMessageImpl) message,
                new MessagePayloadInputstream(InputStream.nullInputStream(), new ObjectMapper()));

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS,
                result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_nullPayload_returnInternalRecipientErrorResponse() {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(
                null, URI.create("https://someUri"), URI.create("https://someUri"));

        /* ASSERT */
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_emptyPayload_returnInternalRecipientErrorResponse() {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(
                "", URI.create("https://someUri"), URI.create("https://someUri"));

        /* ASSERT */
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_invalidPayload_returnInternalRecipientErrorResponse() {
        /* ARRANGE */
        final var payload = "something that is not a contract request.";
        final var messageId = URI.create("https://someUri");
        final var issuerConnector = URI.create("https://someUri");

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(
                payload, messageId, issuerConnector);

        /* ASSERT */
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_contractEmptyRules_returnBadParametersErrorResponse()
            throws IOException {
        /* ARRANGE */
        final var message = new ContractRequestBuilder(URI.create("https://someUri"))
                .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, uri);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS,
                result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_noTargetInRule_returnBadParametersErrorResponse()
            throws IOException {
        /* ARRANGE */
        final var message = new ContractRequestBuilder(URI.create("https://someUri"))
                ._permission_(Util.asList(new PermissionBuilder()
                        ._action_(Util.asList(Action.USE))
                        .build()))
                .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, uri);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_notAValidTarget_returnInternalRecipientErrorResponse()
            throws IOException {
        /* ARRANGE */
        final var message = new ContractRequestBuilder(URI.create("https://someUri"))
                ._permission_(Util.asList(new PermissionBuilder()
                        ._action_(Util.asList(Action.USE))
                        ._target_(URI.create("https://someUri/"))
                        .build()))
                .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, uri);

        /* ASSERT */
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_unknownTarget_returnNotFoundErrorResponse()
            throws IOException {
        /* ARRANGE */
        final var message = new ContractRequestBuilder(URI.create("https://someUri"))
                ._permission_(Util.asList(new PermissionBuilder()
                        ._action_(Util.asList(Action.USE))
                        ._target_(URI.create("https://localhost:8080/api/artifacts/550e8400-e29b" +
                                "-11d4-a716-446655440000"))
                        .build()))
                .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, uri);

        /* ASSERT */
        assertEquals(RejectionReason.NOT_FOUND, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_noContractOffers_returnNotFoundErrorResponse()
            throws IOException {
        /* ARRANGE */
        final var artifactId = URI.create("https://localhost:8080/api/artifacts/550e8400-e29b" +
                "-11d4-a716-446655440000");

        final var message = new ContractRequestBuilder(URI.create("https://someUri"))
                ._permission_(Util.asList(new PermissionBuilder()
                        ._action_(Util.asList(Action.USE))
                        ._target_(artifactId)
                        .build()))
                .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();

        Mockito.doReturn(new ArrayList<Contract>()).when(dependencyResolver).getContractOffersByArtifactId(Mockito.eq(artifactId));

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, uri);

        /* ASSERT */
        assertEquals(RejectionReason.NOT_FOUND, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_onlyRestrictedContracts_returnNotFoundErrorResponse() throws IOException {
        /* ARRANGE */
        final var artifactId = URI.create("https://localhost:8080/api/artifacts/550e8400-e29b" +
                "-11d4-a716-446655440000");

        final var message = new ContractRequestBuilder(URI.create("https://someUri"))
                ._permission_(Util.asList(new PermissionBuilder()
                        ._action_(Util.asList(Action.USE))
                        ._target_(artifactId)
                        .build()))
                .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");

        final var desc = new ContractDesc();
        desc.setConsumer(URI.create("https://someConsumer"));
        final var contract = new ContractFactory().create(desc);

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();
        Mockito.doReturn(Arrays.asList(contract)).when(dependencyResolver).getContractOffersByArtifactId(Mockito.eq(artifactId));

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, uri);

        /* ASSERT */
        assertEquals(RejectionReason.NOT_FOUND, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_invalidRulesInContract_rejectContractWithMalformedMessageResponse() throws IOException {
        /* ARRANGE */
        final var artifactId = URI.create("https://localhost:8080/api/artifacts/550e8400-e29b" +
                "-11d4-a716-446655440000");

        final var message = new ContractRequestBuilder(URI.create("https://someUri"))
                ._permission_(Util.asList(new PermissionBuilder()
                        ._action_(Util.asList(Action.USE))
                        ._target_(artifactId)
                        .build()))
                .build();

        final var payload = new Serializer().serialize(message).replace("idsc:USE", "idsc:DONTNOW");
        final var messageId = URI.create("https://someUri");

        final var desc = new ContractDesc();
        desc.setConsumer(uri);
        final var contract = new ContractFactory().create(desc);

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();
        Mockito.doReturn(Arrays.asList(contract)).when(dependencyResolver).getContractOffersByArtifactId(Mockito.eq(artifactId));
        Mockito.doThrow(IllegalArgumentException.class).when(dependencyResolver).getRulesByContractOffer(Mockito.eq(contract));

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, uri);

        /* ASSERT */
        assertEquals(RejectionReason.MALFORMED_MESSAGE,
                result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_validRequestCannotStore_returnInvalidRecipientError() throws IOException {
        /* ARRANGE */
        final var artifactId = URI.create("https://localhost:8080/api/artifacts/550e8400-e29b" +
                "-11d4-a716-446655440000");

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._target_(artifactId)
                .build();

        final var message = new ContractRequestBuilder(URI.create("https://someUri"))
                ._permission_(Util.asList(permission))
                .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");

        final var contractDesc = new ContractDesc();
        contractDesc.setConsumer(uri);
        final var contract = new ContractFactory().create(contractDesc);

        final var ruleDesc = new ContractRuleDesc();
        ruleDesc.setValue(new Serializer().serialize(permission));
        final var rule = new ContractRuleFactory().create(ruleDesc);

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();
        Mockito.doReturn(Arrays.asList(contract)).when(dependencyResolver).getContractOffersByArtifactId(Mockito.eq(artifactId));
        Mockito.doReturn(Arrays.asList(rule)).when(dependencyResolver).getRulesByContractOffer(Mockito.eq(contract));

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, uri);

        /* ASSERT */
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_validRequest_returnOk() throws IOException {
        /* ARRANGE */
        final var artifactId = URI.create("https://localhost:8080/api/artifacts/550e8400-e29b" +
                "-11d4-a716-446655440000");

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._target_(artifactId)
                .build();

        final var message = new ContractRequestBuilder(URI.create("https://someUri"))
                ._permission_(Util.asList(permission))
                .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");

        final var contractDesc = new ContractDesc();
        contractDesc.setConsumer(uri);
        final var contract = new ContractFactory().create(contractDesc);

        final var ruleDesc = new ContractRuleDesc();
        ruleDesc.setValue(new Serializer().serialize(permission));
        final var rule = new ContractRuleFactory().create(ruleDesc);

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn("4.0.0").when(connectorService).getOutboundModelVersion();
        Mockito.doReturn(Arrays.asList(contract)).when(dependencyResolver).getContractOffersByArtifactId(Mockito.eq(artifactId));
        Mockito.doReturn(Arrays.asList(rule)).when(dependencyResolver).getRulesByContractOffer(Mockito.eq(contract));
        Mockito.doReturn(getContractAgreement()).when(persistenceService).buildAndSaveContractAgreement(
                Mockito.any(), Mockito.eq(Arrays.asList(artifactId)), Mockito.eq(uri));

        /* ACT */
        final var result = (BodyResponse<?>) handler.processContractRequest(payload, messageId, uri);

        /* ASSERT */
        assertTrue(result.getHeader() instanceof ContractAgreementMessage);
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private ContractAgreement getContractAgreement() {
        return new ContractAgreementBuilder(URI.create("http://localhost:8080/api/agreements/" + UUID.randomUUID()))
                ._contractStart_(IdsMessageUtils.getGregorianNow())
                ._contractEnd_(IdsMessageUtils.getGregorianNow())
                .build();
    }

    @SneakyThrows
    private XMLGregorianCalendar getCalender() {
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }
}
