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
package io.dataspaceconnector.service.message.handler;

import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;

import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessage;
import de.fraunhofer.iais.eis.NotificationMessageBuilder;
import de.fraunhofer.iais.eis.NotificationMessageImpl;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.ids.messaging.response.BodyResponse;
import de.fraunhofer.ids.messaging.response.ErrorResponse;
import io.dataspaceconnector.camel.route.handler.IdscpServerRoute;
import io.dataspaceconnector.model.message.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.service.ids.ConnectorService;
import io.dataspaceconnector.service.message.type.MessageProcessedNotificationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class NotificationMessageHandlerTest {

    @MockBean
    private IdscpServerRoute idscpServerRoute;

    @SpyBean
    private ConnectorService connectorService;

    @SpyBean
    MessageProcessedNotificationService notificationService;

    @Autowired
    NotificationMessageHandler handler;

    private final DynamicAttributeToken token = new DynamicAttributeTokenBuilder()
            ._tokenValue_("token")
            ._tokenFormat_(TokenFormat.JWT)
            .build();

    private final String version = "4.0.0";
    private final URI uri = URI.create("https://localhost:8080");

    @SneakyThrows
    @Test
    public void handleMessage_nullMessage_returnBadRequest() {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage(null, null);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS,
                result.getRejectionMessage().getRejectionReason());
    }

    @SneakyThrows
    @Test
    public void handleMessage_nullMessage_returnVersionNotSupported() {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new NotificationMessageBuilder()
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
        final var result = (ErrorResponse) handler.handleMessage((NotificationMessageImpl) message,
                null);

        /* ASSERT */
        assertEquals(RejectionReason.VERSION_NOT_SUPPORTED,
                result.getRejectionMessage().getRejectionReason());
    }

    @SneakyThrows
    @Test
    public void handleMessage_validMsg_returnMessageProcessNotification() {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new NotificationMessageBuilder()
                ._senderAgent_(uri)
                ._issuerConnector_(uri)
                ._securityToken_(token)
                ._modelVersion_(version)
                ._issued_(xmlCalendar)
                .build();

        Mockito.doNothing().when(notificationService).validateIncomingMessage(message);
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(uri).when(connectorService).getConnectorId();
        Mockito.doReturn(version).when(connectorService).getOutboundModelVersion();

        /* ACT */
        final var result = (BodyResponse<?>) handler.handleMessage((NotificationMessageImpl)
                message, null);

        /* ASSERT */
        final var expected = (MessageProcessedNotificationMessage) notificationService
                .buildMessage(new MessageProcessedNotificationMessageDesc(
                        message.getIssuerConnector(), message.getId()));

        // Compare header
        assertEquals(expected.getIssuerConnector(), result.getHeader().getIssuerConnector());
        assertEquals(expected.getAuthorizationToken(), result.getHeader().getAuthorizationToken());
        assertEquals(expected.getComment().toString(), result.getHeader().getComment().toString());
        assertEquals(expected.getCorrelationMessage(), result.getHeader().getCorrelationMessage());
        assertEquals(expected.getContentVersion(), result.getHeader().getContentVersion());
        assertEquals(expected.getLabel().toString(), result.getHeader().getLabel().toString());
        assertEquals(expected.getModelVersion(), result.getHeader().getModelVersion());
        assertEquals(expected.getProperties(), result.getHeader().getProperties());
        assertEquals(expected.getRecipientAgent(), result.getHeader().getRecipientAgent());
        assertEquals(expected.getRecipientConnector(), result.getHeader().getRecipientConnector());
        assertEquals(expected.getSenderAgent(), result.getHeader().getSenderAgent());
        assertEquals(expected.getTransferContract(), result.getHeader().getTransferContract());
    }
}
