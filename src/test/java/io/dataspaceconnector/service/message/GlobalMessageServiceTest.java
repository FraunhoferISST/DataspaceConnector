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
package io.dataspaceconnector.service.message;

import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageBuilder;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.ids.messaging.broker.IDSBrokerService;
import de.fraunhofer.ids.messaging.protocol.multipart.mapping.MessageProcessedNotificationMAP;
import io.dataspaceconnector.model.broker.BrokerFactory;
import io.dataspaceconnector.repository.BrokerRepository;
import io.dataspaceconnector.service.configuration.BrokerService;
import io.dataspaceconnector.service.configuration.EntityLinkerService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.xml.datatype.DatatypeFactory;
import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = GlobalMessageService.class)
class GlobalMessageServiceTest {

    @MockBean
    private IDSBrokerService brokerService;

    @MockBean
    private BrokerRepository brokerRepository;

    @MockBean
    private BrokerFactory brokerFactory;

    @MockBean
    private EntityLinkerService.BrokerOfferedResourcesLinker brokerOfferedResourcesLinker;

    @MockBean
    private BrokerService dscBrokerService;

    @Autowired
    GlobalMessageService messageService;

    @Test
    @SneakyThrows
    public void sendConnectorUpdateMessage_validInputValidResponse_returnTrue() {
        /* ARRANGE */
        Mockito.doReturn(getResponse()).when(brokerService).updateSelfDescriptionAtBroker(Mockito.any());

        /* ACT */
        final var response =
                messageService.sendConnectorUpdateMessage(URI.create("https://recipient"));

        /* ASSERT */
        assertTrue(response);
    }

    @Test
    @SneakyThrows
    public void sendConnectorUpdateMessage_validInputInvalidResponse_returnFalse() {
        /* ARRANGE */
        Mockito.doReturn(null).when(brokerService).updateSelfDescriptionAtBroker(Mockito.any());

        /* ACT */
        final var response =
                messageService.sendConnectorUpdateMessage(URI.create("https://recipient"));

        /* ASSERT */
        assertFalse(response);
    }

    @Test
    @SneakyThrows
    public void sendConnectorUnavailableMessage_validInputValidResponse_returnTrue() {
        /* ARRANGE */
        Mockito.doReturn(getResponse()).when(brokerService).unregisterAtBroker(Mockito.any());

        /* ACT */
        final var response =
                messageService.sendConnectorUnavailableMessage(URI.create("https://recipient"));

        /* ASSERT */
        assertTrue(response);
    }

    @Test
    @SneakyThrows
    public void sendConnectorUnavailableMessage_validInputInvalidResponse_returnFalse() {
        /* ARRANGE */
        Mockito.doReturn(null).when(brokerService).unregisterAtBroker(Mockito.any());

        /* ACT */
        final var response =
                messageService.sendConnectorUnavailableMessage(URI.create("https://recipient"));

        /* ASSERT */
        assertFalse(response);
    }

    @Test
    @SneakyThrows
    public void sendResourceUpdateMessage_validInputValidResponse_returnTrue() {
        /* ARRANGE */
        Mockito.doReturn(getResponse()).when(brokerService).updateResourceAtBroker(Mockito.any(), Mockito.any());

        /* ACT */
        final var response =
                messageService.sendResourceUpdateMessage(URI.create("https://recipient"), new ResourceBuilder().build());

        /* ASSERT */
        assertTrue(response);
    }

    @Test
    @SneakyThrows
    public void sendResourceUpdateMessage_invalidInput_throwNullPointerException() {
        /* ARRANGE */
        Mockito.doReturn(getResponse()).when(brokerService).updateResourceAtBroker(Mockito.any(), Mockito.any());

        /* ACT & ASSERT */
        assertThrows(NullPointerException.class, () -> messageService.sendResourceUpdateMessage(URI.create("https://recipient"), null));
    }

    @Test
    @SneakyThrows
    public void sendResourceUpdateMessage_validInputInvalidResponse_returnFalse() {
        /* ARRANGE */
        Mockito.doReturn(null).when(brokerService).updateResourceAtBroker(Mockito.any(), Mockito.any());

        /* ACT */
        final var response =
                messageService.sendResourceUpdateMessage(URI.create("https://recipient"), new ResourceBuilder().build());

        /* ASSERT */
        assertFalse(response);
    }

    @Test
    @SneakyThrows
    public void sendResourceUnavailableMessage_validInputValidResponse_returnTrue() {
        /* ARRANGE */
        Mockito.doReturn(getResponse()).when(brokerService).removeResourceFromBroker(Mockito.any(), Mockito.any());

        /* ACT */
        final var response =
                messageService.sendResourceUnavailableMessage(URI.create("https://recipient"), new ResourceBuilder().build());

        /* ASSERT */
        assertTrue(response);
    }

    @Test
    @SneakyThrows
    public void sendResourceUnavailableMessage_invalidInput_throwNullPointerException() {
        /* ARRANGE */
        Mockito.doReturn(getResponse()).when(brokerService).removeResourceFromBroker(Mockito.any(), Mockito.any());

        /* ACT & ASSERT */
        assertThrows(NullPointerException.class, () -> messageService.sendResourceUnavailableMessage(URI.create("https://recipient"), null));
    }

    @Test
    @SneakyThrows
    public void sendResourceUnavailableMessage_validInputInvalidResponse_returnFalse() {
        /* ARRANGE */
        Mockito.doReturn(null).when(brokerService).removeResourceFromBroker(Mockito.any(), Mockito.any());

        /* ACT */
        final var response =
                messageService.sendResourceUnavailableMessage(URI.create("https://recipient"), new ResourceBuilder().build());

        /* ASSERT */
        assertFalse(response);
    }

    @SneakyThrows
    private MessageProcessedNotificationMAP getResponse() {
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        final var connectorId = URI.create("https://connector");
        final var modelVersion = "4.0.0";
        final var token = new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build();
        return new MessageProcessedNotificationMAP(
                new MessageProcessedNotificationMessageBuilder()
                        ._securityToken_(token)
                        ._modelVersion_(modelVersion)
                        ._issuerConnector_(connectorId)
                        ._correlationMessage_(URI.create("https://message"))
                        ._senderAgent_(connectorId)
                        ._issued_(xmlCalendar)
                        .build());
    }
}
