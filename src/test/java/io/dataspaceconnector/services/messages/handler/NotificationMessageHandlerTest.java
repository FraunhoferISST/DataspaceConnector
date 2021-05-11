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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;

import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessage;
import de.fraunhofer.iais.eis.NotificationMessageBuilder;
import de.fraunhofer.iais.eis.NotificationMessageImpl;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.TokenFormat;
import io.dataspaceconnector.model.messages.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.services.messages.types.MessageProcessedNotificationService;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class NotificationMessageHandlerTest {

    @SpyBean
    MessageProcessedNotificationService notificationService;

    @Autowired
    NotificationMessageHandler handler;

    @Test
    public void handleMessage_nullMessage_returnBadRequest() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage(null, null);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_nullMessage_returnVersionNotSupported() throws DatatypeConfigurationException {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new NotificationMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("tetris")
                ._issued_(xmlCalendar)
                .build();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage((NotificationMessageImpl) message, null);

        /* ASSERT */
        assertEquals(RejectionReason.VERSION_NOT_SUPPORTED, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_validMsg_returnMessageProcessNotification() throws DatatypeConfigurationException {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new NotificationMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                .build();

        /* ACT */
        final var result = (BodyResponse) handler.handleMessage((NotificationMessageImpl) message, null);

        /* ASSERT */
        final var expected = (MessageProcessedNotificationMessage) notificationService.buildMessage(new MessageProcessedNotificationMessageDesc(
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
