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
package io.dataspaceconnector.service.message.handler.type;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;
import javax.xml.datatype.DatatypeFactory;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DescriptionRequestMessageImpl;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.response.BodyResponse;
import de.fraunhofer.ids.messaging.response.ErrorResponse;
import de.fraunhofer.ids.messaging.response.MessageResponse;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import io.dataspaceconnector.model.message.DescriptionResponseMessageDesc;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.service.message.builder.type.DescriptionResponseService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
 class DescriptionRequestHandlerTest {

    @Autowired
    DescriptionResponseService messageService;

    @SpyBean
    ConnectorService connectorService;

    @MockBean
    EntityResolver resolver;

    @Autowired
    DescriptionRequestHandler handler;

    @BeforeEach
    void init() {
        when(connectorService.getCurrentDat()).thenReturn(new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.JWT)
                ._tokenValue_("value")
                .build());
    }

    @SneakyThrows
    @Test
    public void handleMessage_validSelfDescriptionMsg_returnSelfDescription() {
        /* ARRANGE */
        final var connector = new BaseConnectorBuilder()
                ._resourceCatalog_(new ArrayList<>())
                ._outboundModelVersion_("4.0.0")
                ._inboundModelVersion_(Util.asList("4.0.0"))
                ._maintainer_(URI.create("https://someMaintainer"))
                ._curator_(URI.create("https://someCurator"))
                ._hasDefaultEndpoint_(new ConnectorEndpointBuilder(
                        URI.create("https://someEndpoint"))
                        ._accessURL_(URI.create("https://someAccessUrl"))
                        .build())
                ._securityProfile_(SecurityProfile.BASE_SECURITY_PROFILE)
                .build();

        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new DescriptionRequestMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()
                        ._tokenFormat_(TokenFormat.OTHER)
                        ._tokenValue_("")
                        .build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                .build();

        Mockito.doReturn(connector).when(connectorService).getConnectorWithOfferedResources();

         /* ACT */
         final var result =
                 (BodyResponse<?>) handler.handleMessage((DescriptionRequestMessageImpl) message, null);

         /* ASSERT */
         final var expected = (BodyResponse<?>) constructSelfDescription(
                 message.getIssuerConnector(), message.getId());

        // Compare payload
        assertEquals(expected.getPayload(), result.getPayload());

        // Compare header
        assertEquals(
                expected.getHeader().getIssuerConnector(), result.getHeader().getIssuerConnector());
        assertEquals(expected.getHeader().getAuthorizationToken(),
                result.getHeader().getAuthorizationToken());
        assertEquals(expected.getHeader().getComment().toString(),
                result.getHeader().getComment().toString());
        assertEquals(expected.getHeader().getCorrelationMessage(),
                result.getHeader().getCorrelationMessage());
        assertEquals(
                expected.getHeader().getContentVersion(), result.getHeader().getContentVersion());
        assertEquals(expected.getHeader().getLabel().toString(),
                result.getHeader().getLabel().toString());
        assertEquals(expected.getHeader().getModelVersion(), result.getHeader().getModelVersion());
        assertEquals(expected.getHeader().getProperties(), result.getHeader().getProperties());
        assertEquals(
                expected.getHeader().getRecipientAgent(), result.getHeader().getRecipientAgent());
        assertEquals(expected.getHeader().getRecipientConnector(),
                result.getHeader().getRecipientConnector());
        assertEquals(expected.getHeader().getSenderAgent(), result.getHeader().getSenderAgent());
        assertEquals(expected.getHeader().getTransferContract(),
                result.getHeader().getTransferContract());
    }

    @SneakyThrows
    @Test
    public void handleMessage_validResourceDescriptionMsgKnownId_returnResourceDescription() {
        /* ARRANGE */
        final var artifact = new ArtifactFactory().create(new ArtifactDesc());

        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message =
                new DescriptionRequestMessageBuilder()
                        ._senderAgent_(URI.create("https://localhost:8080"))
                        ._issuerConnector_(URI.create("https://localhost:8080"))
                        ._securityToken_(new DynamicAttributeTokenBuilder()
                                ._tokenFormat_(TokenFormat.OTHER)
                                ._tokenValue_("")
                                .build())
                        ._modelVersion_("4.0.0")
                        ._requestedElement_(URI.create("https://localhost/8080/api/artifacts/"))
                        ._issued_(xmlCalendar)
                        .build();

        when(resolver.getEntityById(Mockito.eq(message.getRequestedElement())))
                .thenReturn(Optional.of(artifact));
        when(resolver.getEntityAsRdfString(artifact)).thenReturn(getArtifact().toRdf());

         /* ACT */
         final var result =
                 (BodyResponse<?>) handler.handleMessage((DescriptionRequestMessageImpl) message, null);

         /* ASSERT */
         final var expected = (BodyResponse<?>) constructResourceDescription(
                 message.getRequestedElement(), message.getIssuerConnector(), message.getId());

        // Compare payload
        assertEquals(expected.getPayload(), result.getPayload());

        // Compare header
        assertEquals(
                expected.getHeader().getIssuerConnector(), result.getHeader().getIssuerConnector());
        assertEquals(expected.getHeader().getAuthorizationToken(),
                result.getHeader().getAuthorizationToken());
        assertEquals(expected.getHeader().getComment().toString(),
                result.getHeader().getComment().toString());
        assertEquals(expected.getHeader().getCorrelationMessage(),
                result.getHeader().getCorrelationMessage());
        assertEquals(
                expected.getHeader().getContentVersion(), result.getHeader().getContentVersion());
        assertEquals(expected.getHeader().getLabel().toString(),
                result.getHeader().getLabel().toString());
        assertEquals(expected.getHeader().getModelVersion(), result.getHeader().getModelVersion());
        assertEquals(expected.getHeader().getProperties(), result.getHeader().getProperties());
        assertEquals(
                expected.getHeader().getRecipientAgent(), result.getHeader().getRecipientAgent());
        assertEquals(expected.getHeader().getRecipientConnector(),
                result.getHeader().getRecipientConnector());
        assertEquals(expected.getHeader().getSenderAgent(), result.getHeader().getSenderAgent());
        assertEquals(expected.getHeader().getTransferContract(),
                result.getHeader().getTransferContract());
    }

    @SneakyThrows
    @Test
    public void handleMessage_nullMessage_returnBadParametersRejectionMessage() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage(null, null);

        /* ASSERT */
        assertEquals(
                RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @SneakyThrows
    @Test
    public void handleMessage_unsupportedMessage_returnUnsupportedVersionRejectionMessage() {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message =
                new DescriptionRequestMessageBuilder()
                        ._senderAgent_(URI.create("https://localhost:8080"))
                        ._issuerConnector_(URI.create("https://localhost:8080"))
                        ._securityToken_(new DynamicAttributeTokenBuilder()
                                ._tokenFormat_(TokenFormat.OTHER)
                                ._tokenValue_("")
                                .build())
                        ._modelVersion_("tetris")
                        ._requestedElement_(URI.create("https://localhost/8080/api/artifacts/"))
                        ._issued_(xmlCalendar)
                        .build();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage(
                (DescriptionRequestMessageImpl) message, null);

        /* ASSERT */
        assertEquals(RejectionReason.VERSION_NOT_SUPPORTED,
                result.getRejectionMessage().getRejectionReason());
    }

    @SneakyThrows
    @Test
    public void handleMessage_validResourceDescriptionMsgUnknownId_returnNotFoundRejectionReason() {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message =
                new DescriptionRequestMessageBuilder()
                        ._senderAgent_(URI.create("https://localhost:8080"))
                        ._issuerConnector_(URI.create("https://localhost:8080"))
                        ._securityToken_(new DynamicAttributeTokenBuilder()
                                ._tokenFormat_(TokenFormat.OTHER)
                                ._tokenValue_("")
                                .build())
                        ._modelVersion_("4.0.0")
                        ._requestedElement_(URI.create("https://localhost/8080/api/artifacts/"))
                        ._issued_(xmlCalendar)
                        .build();

        when(resolver.getEntityById(Mockito.eq(message.getRequestedElement())))
                .thenThrow(ResourceNotFoundException.class);

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage(
                (DescriptionRequestMessageImpl) message, null);

        /* ASSERT */
        assertEquals(RejectionReason.NOT_FOUND, result.getRejectionMessage().getRejectionReason());
    }

     @SneakyThrows
     @Test
     public void handleMessage_validResourceDescriptionMsgEmptyEntity_returnNotFoundRejection() {
         /* ARRANGE */
         final var calendar = new GregorianCalendar();
         calendar.setTime(new Date());
         final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

         final var message =
                 new DescriptionRequestMessageBuilder()
                         ._senderAgent_(URI.create("https://localhost:8080"))
                         ._issuerConnector_(URI.create("https://localhost:8080"))
                         ._securityToken_(new DynamicAttributeTokenBuilder()
                                 ._tokenFormat_(TokenFormat.OTHER)
                                 ._tokenValue_("")
                                 .build())
                         ._modelVersion_("4.0.0")
                         ._requestedElement_(URI.create("https://localhost/8080/api/artifacts/"))
                         ._issued_(xmlCalendar)
                         .build();

         Mockito.doReturn(Optional.empty()).when(resolver).getEntityById(Mockito.any());

         /* ACT */
         final var result = (ErrorResponse) handler.handleMessage((DescriptionRequestMessageImpl) message, null);

         /* ASSERT */
         assertEquals(RejectionReason.NOT_FOUND, result.getRejectionMessage().getRejectionReason());
     }

    @SneakyThrows
    private MessageResponse constructSelfDescription(final URI issuer, final URI messageId) {
        final var connector = connectorService.getConnectorWithOfferedResources();

        // Build ids response message.
        final var desc = new DescriptionResponseMessageDesc(issuer, messageId);
        final var header = messageService.buildMessage(desc);

        // Send ids response message.
        return BodyResponse.create(header, connector.toRdf());
    }

    @SneakyThrows
    private MessageResponse constructResourceDescription(final URI requested,
                                                         final URI issuer,
                                                         final URI messageId) {
        final var entity = resolver.getEntityById(requested);

        if (entity != null) {
            // If the element has been found, build the ids response message.
            final var desc = new DescriptionResponseMessageDesc(issuer, messageId);
            final var header = messageService.buildMessage(desc);
            final var payload = resolver.getEntityAsRdfString(entity.get());

            // Send ids response message.
            return BodyResponse.create(header, payload);
        } else {
            return null;
        }
    }

    private Artifact getArtifact() {
        return new ArtifactBuilder(URI.create("http://id.com")).build();
    }
}
