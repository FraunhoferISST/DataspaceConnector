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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import javax.xml.datatype.DatatypeFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageImpl;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.RequestMessageBuilder;
import de.fraunhofer.iais.eis.RequestMessageImpl;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.ids.messaging.handler.message.MessagePayloadInputstream;
import de.fraunhofer.ids.messaging.response.BodyResponse;
import de.fraunhofer.ids.messaging.response.ErrorResponse;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.model.subscription.SubscriptionDesc;
import io.dataspaceconnector.service.EntityResolver;
import io.dataspaceconnector.service.message.builder.type.MessageProcessedNotificationService;
import io.dataspaceconnector.service.resource.type.SubscriptionService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
class SubscriptionRequestHandlerTest {

   @Autowired
   MessageProcessedNotificationService messageService;

   @SpyBean
   ConnectorService connectorService;

   @MockBean
   EntityResolver resolver;

   @MockBean
   SubscriptionService subscriptionService;

   @Autowired
   SubscriptionMessageHandler handler;

    @BeforeEach
    void init() {
        when(connectorService.getCurrentDat()).thenReturn(new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.JWT)
                ._tokenValue_("value")
                .build());
    }

    @SneakyThrows
    @Test
    public void handleMessage_missingTarget_returnInternalRecipientErrorResponse() {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        final var message = new RequestMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                .build();
        final var payload = new MessagePayloadInputstream(InputStream.nullInputStream(),
                new ObjectMapper());

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage((RequestMessageImpl) message, payload);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                result.getRejectionMessage().getRejectionReason());
    }

    @SneakyThrows
    @Test
    public void handleMessage_invalidPayload_returnBadParametersResponse() {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        final var message = new RequestMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                .build();
        message.setProperty("https://w3id.org/idsa/core/target", "1234:https://target/");
        final var payload = new MessagePayloadInputstream(
                new ByteArrayInputStream(getSubscription().toString().getBytes()), new ObjectMapper());

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage((RequestMessageImpl) message, payload);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS,
                result.getRejectionMessage().getRejectionReason());
    }

    @SneakyThrows
    @Test
    public void handleMessage_validInput_returnSuccessfulSubscription() {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        final var message = new RequestMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                .build();
        message.setProperty("https://w3id.org/idsa/core/target", "1234:https://target/");

        final var objectMapper = new ObjectMapper();
        final var string = objectMapper.writeValueAsString(getSubscription());
        final var payload = new MessagePayloadInputstream(
                new ByteArrayInputStream(string.getBytes()), new ObjectMapper());

        Mockito.doReturn(getSubscription()).when(subscriptionService).create(getDesc());

        /* ACT */
        final var result = (BodyResponse<?>) handler.handleMessage((RequestMessageImpl) message, payload);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(MessageProcessedNotificationMessageImpl.class, result.getHeader().getClass());
    }

    @SneakyThrows
    @Test
    public void handleMessage_validInput_returnSuccessfulUnsubscription() {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        final var message = new RequestMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                .build();
        message.setProperty("https://w3id.org/idsa/core/target", "1234:https://target/");

        final var objectMapper = new ObjectMapper();
        final var string = objectMapper.writeValueAsString(null);
        final var payload = new MessagePayloadInputstream(
                new ByteArrayInputStream(string.getBytes()), new ObjectMapper());

        Mockito.doReturn(getSubscription()).when(subscriptionService).create(getDesc());

        /* ACT */
        final var result = (BodyResponse<?>) handler.handleMessage((RequestMessageImpl) message, payload);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(MessageProcessedNotificationMessageImpl.class, result.getHeader().getClass());
    }

    private SubscriptionDesc getDesc() {
        var desc = new SubscriptionDesc();
        desc.setTitle("The new title.");
        desc.setDescription("Description.");
        desc.setLocation(URI.create("https://location"));
        desc.setSubscriber(URI.create("https://subscriber"));
        desc.setTarget(URI.create("https://target"));
        desc.setPushData(false);

        return desc;
    }

    @SneakyThrows
    private Subscription getSubscription() {
        final var desc = getDesc();

        final var constructor = Subscription.class.getConstructor();

        final var subscription = constructor.newInstance();
        ReflectionTestUtils.setField(subscription, "id", UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));
        ReflectionTestUtils.setField(subscription, "title", desc.getTitle());
        ReflectionTestUtils.setField(subscription, "description", desc.getDescription());
        ReflectionTestUtils.setField(subscription, "target", desc.getTarget());
        ReflectionTestUtils.setField(subscription, "location", desc.getLocation());
        ReflectionTestUtils.setField(subscription, "subscriber", desc.getSubscriber());
        ReflectionTestUtils.setField(subscription, "pushData", desc.isPushData());

        return subscription;
    }
}
