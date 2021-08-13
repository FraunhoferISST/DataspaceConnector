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
package io.dataspaceconnector.service.message.builder.type;

import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RejectionMessageBuilder;
import de.fraunhofer.iais.eis.RejectionMessageImpl;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.protocol.http.IdsHttpService;
import de.fraunhofer.ids.messaging.protocol.multipart.parser.MultipartParseException;
import io.dataspaceconnector.common.exception.MessageException;
import io.dataspaceconnector.common.exception.MessageResponseException;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.model.message.DescriptionRequestMessageDesc;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {DescriptionRequestService.class})
class DescriptionRequestServiceTest {

    @MockBean
    private ConnectorService connectorService;

    @MockBean
    private IdsHttpService idsHttpService;

    @MockBean
    private DeserializationService deserializationService;

    @Autowired
    private DescriptionRequestService requestService;

    @Test
    public void buildMessage_null_throwsIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> requestService.buildMessage(null));
    }

    @Test
    public void buildMessage_validDesc_returnValidMessage() {
        /* ARRANGE */
        final var desc = new DescriptionRequestMessageDesc();
        desc.setRecipient(URI.create("https://recipient"));
        desc.setRequestedElement(URI.create("https://requestedElement"));

        final var connectorId = URI.create("https://connector");
        final var modelVersion = "4.0.0";
        final var token = new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build();
        Mockito.when(connectorService.getConnectorId()).thenReturn(connectorId);
        Mockito.when(connectorService.getOutboundModelVersion()).thenReturn(modelVersion);
        Mockito.when(connectorService.getCurrentDat()).thenReturn(token);

        /* ACT */
        final var result = (DescriptionRequestMessage) requestService.buildMessage(desc);

        /* ASSERT */
        assertEquals(1, result.getRecipientConnector().size());
        assertEquals(desc.getRecipient(), result.getRecipientConnector().get(0));
        assertEquals(desc.getRequestedElement(), result.getRequestedElement());
        assertEquals(connectorId, result.getIssuerConnector());
        assertEquals(modelVersion, result.getModelVersion());
        assertEquals(token, result.getSecurityToken());
    }

    @Test
    public void getResponseContent_inputRejectionMessage_returnMap() {
        /* ARRANGE */
        final var msg = getRejectionMessage();

        /* ACT */
        final var result = requestService.getResponseContent(msg, "payload");

        /* ASSERT */
        assertNotNull(result);
        assertEquals(RejectionMessageImpl.class, result.get("type"));
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR, result.get("reason"));
        assertEquals("payload", result.get("payload"));
    }

    @Test
    public void getResponseContent_invalidInput_throwMessageResponseException() {
        /* ARRANGE */
        final var map = new HashMap<String, String>() {{
            put("header", "header");
            put("payload", "payload");
        }};

        /* ACT & ASSERT */
        assertThrows(MessageResponseException.class, () -> requestService.getResponseContent(map));
    }

    @Test
    public void getResponseContent_validInput_returnMap() {
        /* ARRANGE */
        final var rdf = getRejectionMessage().toRdf();
        final var map = new HashMap<String, String>() {{
            put("header", rdf);
            put("payload", "payload");
        }};

        Mockito.doReturn(getRejectionMessage()).when(deserializationService).getMessage(rdf);

        /* ACT */
        final var result = requestService.getResponseContent(map);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(RejectionMessageImpl.class, result.get("type"));
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR, result.get("reason"));
        assertEquals("payload", result.get("payload"));
    }

    @Test
    @SneakyThrows
    public void send_validInput_returnEmptyResponseMap() {
        /* ARRANGE */
        final var desc = new DescriptionRequestMessageDesc();
        desc.setRecipient(URI.create("https://recipient"));
        desc.setRequestedElement(URI.create("https://requestedElement"));

        final var connectorId = URI.create("https://connector");
        final var modelVersion = "4.0.0";
        final var token = new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build();
        Mockito.when(connectorService.getConnectorId()).thenReturn(connectorId);
        Mockito.when(connectorService.getOutboundModelVersion()).thenReturn(modelVersion);
        Mockito.when(connectorService.getCurrentDat()).thenReturn(token);

        Mockito.doReturn(new HashMap<>()).when(idsHttpService).sendAndCheckDat(Mockito.any(), Mockito.any());

        /* ACT */
        final var result = requestService.send(desc, null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @SneakyThrows
    public void send_thrownMultipartParseException_throwMessageResponseException() {
        /* ARRANGE */
        final var desc = new DescriptionRequestMessageDesc();
        desc.setRecipient(URI.create("https://recipient"));
        desc.setRequestedElement(URI.create("https://requestedElement"));

        final var connectorId = URI.create("https://connector");
        final var modelVersion = "4.0.0";
        final var token = new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build();
        Mockito.when(connectorService.getConnectorId()).thenReturn(connectorId);
        Mockito.when(connectorService.getOutboundModelVersion()).thenReturn(modelVersion);
        Mockito.when(connectorService.getCurrentDat()).thenReturn(token);

        Mockito.doThrow(new MultipartParseException("")).when(idsHttpService).sendAndCheckDat(Mockito.any(), Mockito.any());

        /* ACT & ASSERT */
        assertThrows(MessageResponseException.class, () -> requestService.send(desc, null));
    }

    @Test
    @SneakyThrows
    public void send_thrownConstraintViolationException_throwMessageException() {
        /* ARRANGE */
        final var desc = new DescriptionRequestMessageDesc();
        desc.setRecipient(URI.create("https://recipient"));
        desc.setRequestedElement(URI.create("https://requestedElement"));

        final var connectorId = URI.create("https://connector");
        final var modelVersion = "4.0.0";
        final var token = new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build();
        Mockito.when(connectorService.getConnectorId()).thenReturn(connectorId);
        Mockito.when(connectorService.getOutboundModelVersion()).thenReturn(modelVersion);
        Mockito.when(connectorService.getCurrentDat()).thenReturn(token);

        Mockito.doThrow(new ConstraintViolationException(new ArrayList<>())).when(idsHttpService).sendAndCheckDat(Mockito.any(), Mockito.any());

        /* ACT & ASSERT */
        assertThrows(MessageException.class, () -> requestService.send(desc, null));
    }

    @Test
    @SneakyThrows
    public void send_thrownSocketTimeoutException_throwMessageException() {
        /* ARRANGE */
        final var desc = new DescriptionRequestMessageDesc();
        desc.setRecipient(URI.create("https://recipient"));
        desc.setRequestedElement(URI.create("https://requestedElement"));

        final var connectorId = URI.create("https://connector");
        final var modelVersion = "4.0.0";
        final var token = new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build();
        Mockito.when(connectorService.getConnectorId()).thenReturn(connectorId);
        Mockito.when(connectorService.getOutboundModelVersion()).thenReturn(modelVersion);
        Mockito.when(connectorService.getCurrentDat()).thenReturn(token);

        Mockito.doThrow(new SocketTimeoutException()).when(idsHttpService).sendAndCheckDat(Mockito.any(), Mockito.any());

        /* ACT & ASSERT */
        assertThrows(MessageException.class, () -> requestService.send(desc, null));
    }

    @Test
    @SneakyThrows
    public void send_thrownClaimsException_throwMessageException() {
        /* ARRANGE */
        final var desc = new DescriptionRequestMessageDesc();
        desc.setRecipient(URI.create("https://recipient"));
        desc.setRequestedElement(URI.create("https://requestedElement"));

        final var connectorId = URI.create("https://connector");
        final var modelVersion = "4.0.0";
        final var token = new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build();
        Mockito.when(connectorService.getConnectorId()).thenReturn(connectorId);
        Mockito.when(connectorService.getOutboundModelVersion()).thenReturn(modelVersion);
        Mockito.when(connectorService.getCurrentDat()).thenReturn(token);

        Mockito.doThrow(new ClaimsException("")).when(idsHttpService).sendAndCheckDat(Mockito.any(), Mockito.any());

        /* ACT & ASSERT */
        assertThrows(MessageException.class, () -> requestService.send(desc, null));
    }

    @Test
    @SneakyThrows
    public void send_thrownIOException_throwMessageException() {
        /* ARRANGE */
        final var desc = new DescriptionRequestMessageDesc();
        desc.setRecipient(URI.create("https://recipient"));
        desc.setRequestedElement(URI.create("https://requestedElement"));

        final var connectorId = URI.create("https://connector");
        final var modelVersion = "4.0.0";
        final var token = new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build();
        Mockito.when(connectorService.getConnectorId()).thenReturn(connectorId);
        Mockito.when(connectorService.getOutboundModelVersion()).thenReturn(modelVersion);
        Mockito.when(connectorService.getCurrentDat()).thenReturn(token);

        Mockito.doThrow(new IOException()).when(idsHttpService).sendAndCheckDat(Mockito.any(), Mockito.any());

        /* ACT & ASSERT */
        assertThrows(MessageException.class, () -> requestService.send(desc, null));
    }

    @SneakyThrows
    private RejectionMessage getRejectionMessage() {
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        final var connectorId = URI.create("https://connector");
        final var modelVersion = "4.0.0";
        final var token = new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build();
        return new RejectionMessageBuilder()
                ._securityToken_(token)
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._correlationMessage_(URI.create("https://message"))
                ._senderAgent_(connectorId)
                ._issued_(xmlCalendar)
                ._rejectionReason_(RejectionReason.INTERNAL_RECIPIENT_ERROR)
                .build();
    }
}
