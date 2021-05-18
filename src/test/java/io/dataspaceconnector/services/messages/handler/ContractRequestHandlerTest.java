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
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.model.Contract;
import io.dataspaceconnector.model.ContractDesc;
import io.dataspaceconnector.model.ContractFactory;
import io.dataspaceconnector.model.ContractRuleDesc;
import io.dataspaceconnector.model.ContractRuleFactory;
import io.dataspaceconnector.services.EntityPersistenceService;
import io.dataspaceconnector.services.resources.EntityDependencyResolver;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayloadImpl;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.util.IDSUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
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

    @SpyBean
    EntityPersistenceService persistenceService;

    @SpyBean
    EntityDependencyResolver dependencyResolver;

    @Autowired
    ContractRequestHandler handler;

    @Test
    public void handleMessage_nullMessage_returnBadParametersResponse() {
        /* ARRANGE */
        final var payload = new MessagePayloadImpl(InputStream.nullInputStream(), new ObjectMapper());

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage(null, payload);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_unsupportedMessage_returnUnsupportedVersionRejectionMessage() throws
            DatatypeConfigurationException {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new ContractRequestMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("tetris")
                ._issued_(xmlCalendar)
                .build();

        /* ACT */
        final var result = (ErrorResponse)handler.handleMessage((ContractRequestMessageImpl) message, null);

        /* ASSERT */
        assertEquals(RejectionReason.VERSION_NOT_SUPPORTED, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_nullPayload_returnBadRequestErrorResponse() throws
            DatatypeConfigurationException {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new ContractRequestMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                .build();

        /* ACT */
        final var result = (ErrorResponse)handler.handleMessage((ContractRequestMessageImpl) message, null);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_emptyPayload_returnBadRequestErrorResponse() throws
            DatatypeConfigurationException {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new ContractRequestMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                .build();

        /* ACT */
        final var result = (ErrorResponse)handler.handleMessage((ContractRequestMessageImpl) message, new MessagePayloadImpl(InputStream.nullInputStream(), new ObjectMapper()));

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_nullPayload_returnInternalRecipientErrorResponse() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = (ErrorResponse)handler.processContractRequest(null, URI.create("https://someUri"), URI.create("https://someUri"));

        /* ASSERT */
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_emptyPayload_returnInternalRecipientErrorResponse() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = (ErrorResponse)handler.processContractRequest("", URI.create("https://someUri"), URI.create("https://someUri"));

        /* ASSERT */
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_invalidPayload_returnInternalRecipientErrorResponse() {
        /* ARRANGE */
        final var payload = "something that is not a contract request.";
        final var messageId = URI.create("https://someUri");
        final var issuerConnector = URI.create("https://someUri");

        /* ACT */
        final var result = (ErrorResponse)handler.processContractRequest(payload, messageId, issuerConnector);

        /* ASSERT */
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_contractEmptyRules_returnBadParametersErrorResponse()
            throws IOException {
        /* ARRANGE */
        final var message =
                new ContractRequestBuilder(URI.create("https://someUri"))
                        .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");
        final var issuerConnector = URI.create("https://someUri");

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, issuerConnector);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_noTargetInRule_returnBadParametersErrorResponse()
            throws IOException {
        /* ARRANGE */
        final var message =
                new ContractRequestBuilder(URI.create("https://someUri"))
                        ._permission_(Util.asList(new PermissionBuilder()
                                ._action_(Util.asList(Action.USE))
                                                          .build()))
                        .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");
        final var issuerConnector = URI.create("https://someUri");

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, issuerConnector);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_notAValidTarget_returnInternalRecipientErrorResponse()
            throws IOException {
        /* ARRANGE */
        final var message =
                new ContractRequestBuilder(URI.create("https://someUri"))
                        ._permission_(Util.asList(new PermissionBuilder()
                                                          ._action_(Util.asList(Action.USE))
                                                          ._target_(URI.create("https://someUri/"))
                                                          .build()))
                        .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");
        final var issuerConnector = URI.create("https://someUri");

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, issuerConnector);

        /* ASSERT */
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_unknownTarget_returnNotFoundErrorResponse()
            throws IOException {
        /* ARRANGE */
        final var message =
                new ContractRequestBuilder(URI.create("https://someUri"))
                        ._permission_(Util.asList(new PermissionBuilder()
                                                          ._action_(Util.asList(Action.USE))
                                                          ._target_(URI.create("https://localhost:8080/api/artifacts/550e8400-e29b-11d4-a716-446655440000"))
                                                          .build()))
                        .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");
        final var issuerConnector = URI.create("https://localhost:8080");

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, issuerConnector);

        /* ASSERT */
        assertEquals(RejectionReason.NOT_FOUND, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_noContractOffers_returnNotFoundErrorResponse()
            throws IOException {
        /* ARRANGE */
        final var artifactId = URI.create("https://localhost:8080/api/artifacts/550e8400-e29b-11d4-a716-446655440000");

        final var message =
                new ContractRequestBuilder(URI.create("https://someUri"))
                        ._permission_(Util.asList(new PermissionBuilder()
                                                          ._action_(Util.asList(Action.USE))
                                                          ._target_(artifactId)
                                                          .build()))
                        .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");
        final var issuerConnector = URI.create("https://localhost:8080");

        Mockito.doReturn(new ArrayList<Contract>()).when(dependencyResolver).getContractOffersByArtifactId(Mockito.eq(artifactId));

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, issuerConnector);

        /* ASSERT */
        assertEquals(RejectionReason.NOT_FOUND, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_onlyRestrictedContracts_returnNotFoundErrorResponse() throws IOException {
        /* ARRANGE */
        final var artifactId = URI.create("https://localhost:8080/api/artifacts/550e8400-e29b-11d4-a716-446655440000");

        final var message =
                new ContractRequestBuilder(URI.create("https://someUri"))
                        ._permission_(Util.asList(new PermissionBuilder()
                                                          ._action_(Util.asList(Action.USE))
                                                          ._target_(artifactId)
                                                          .build()))
                        .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");
        final var issuerConnector = URI.create("https://localhost:8080");

        final var desc = new ContractDesc();
        desc.setConsumer(URI.create("https://someConsumer"));
        final var contract = new ContractFactory().create(desc);

        Mockito.doReturn(Arrays.asList(contract)).when(dependencyResolver).getContractOffersByArtifactId(Mockito.eq(artifactId));

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, issuerConnector);

        /* ASSERT */
        assertEquals(RejectionReason.NOT_FOUND, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_invalidRulesInContract_rejectContractWithMalformedMessageResponse() throws IOException {
        /* ARRANGE */
        final var artifactId = URI.create("https://localhost:8080/api/artifacts/550e8400-e29b-11d4-a716-446655440000");

        final var message =
                new ContractRequestBuilder(URI.create("https://someUri"))
                        ._permission_(Util.asList(new PermissionBuilder()
                                                          ._action_(Util.asList(Action.USE))
                                                          ._target_(artifactId)
                                                          .build()))
                        .build();

        final var payload = new Serializer().serialize(message).replace("idsc:USE", "idsc:DONTNOW");
        final var messageId = URI.create("https://someUri");
        final var issuerConnector = URI.create("https://localhost:8080");

        final var desc = new ContractDesc();
        desc.setConsumer(issuerConnector);
        final var contract = new ContractFactory().create(desc);

        Mockito.doReturn(Arrays.asList(contract)).when(dependencyResolver).getContractOffersByArtifactId(Mockito.eq(artifactId));
        Mockito.doThrow(IllegalArgumentException.class).when(dependencyResolver).getRulesByContractOffer(Mockito.eq(contract));

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, issuerConnector);

        /* ASSERT */
        assertEquals(RejectionReason.MALFORMED_MESSAGE, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_validRequestCannotStore_returnInvalidRecipientError() throws IOException {
        /* ARRANGE */
        final var artifactId = URI.create("https://localhost:8080/api/artifacts/550e8400-e29b-11d4-a716-446655440000");

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._target_(artifactId)
                .build();

        final var message =
                new ContractRequestBuilder(URI.create("https://someUri"))
                        ._permission_(Util.asList(permission))
                        .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");
        final var issuerConnector = URI.create("https://localhost:8080");

        final var contractDesc = new ContractDesc();
        contractDesc.setConsumer(issuerConnector);
        final var contract = new ContractFactory().create(contractDesc);

        final var ruleDesc = new ContractRuleDesc();
        ruleDesc.setValue(new Serializer().serialize(permission));
        final var rule = new ContractRuleFactory().create(ruleDesc);

        Mockito.doReturn(Arrays.asList(contract)).when(dependencyResolver).getContractOffersByArtifactId(Mockito.eq(artifactId));
        Mockito.doReturn(Arrays.asList(rule)).when(dependencyResolver).getRulesByContractOffer(Mockito.eq(contract));

        /* ACT */
        final var result = (ErrorResponse) handler.processContractRequest(payload, messageId, issuerConnector);

        /* ASSERT */
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void checkContractRequest_validRequest_returnOk() throws IOException {
        /* ARRANGE */
        final var artifactId = URI.create("https://localhost:8080/api/artifacts/550e8400-e29b-11d4-a716-446655440000");

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._target_(artifactId)
                .build();

        final var message =
                new ContractRequestBuilder(URI.create("https://someUri"))
                        ._permission_(Util.asList(permission))
                        .build();

        final var payload = new Serializer().serialize(message);
        final var messageId = URI.create("https://someUri");
        final var issuerConnector = URI.create("https://localhost:8080");

        final var contractDesc = new ContractDesc();
        contractDesc.setConsumer(issuerConnector);
        final var contract = new ContractFactory().create(contractDesc);

        final var ruleDesc = new ContractRuleDesc();
        ruleDesc.setValue(new Serializer().serialize(permission));
        final var rule = new ContractRuleFactory().create(ruleDesc);

        Mockito.doReturn(Arrays.asList(contract)).when(dependencyResolver).getContractOffersByArtifactId(Mockito.eq(artifactId));
        Mockito.doReturn(Arrays.asList(rule)).when(dependencyResolver).getRulesByContractOffer(Mockito.eq(contract));
        Mockito.doReturn(getContractAgreement()).when(persistenceService).buildAndSaveContractAgreement(
                Mockito.any(), Mockito.eq(Arrays.asList(artifactId)), Mockito.eq(issuerConnector));

        /* ACT */
        final var result = (BodyResponse) handler.processContractRequest(payload, messageId, issuerConnector);

        /* ASSERT */
        assertTrue(result.getHeader() instanceof ContractAgreementMessage);
    }

    private ContractAgreement getContractAgreement() {
        return new ContractAgreementBuilder(URI.create("http://localhost:8080/api/agreements/" + UUID.randomUUID()))
                ._contractStart_(IDSUtils.getGregorianNow())
                ._contractEnd_(IDSUtils.getGregorianNow())
                .build();
    }
}
