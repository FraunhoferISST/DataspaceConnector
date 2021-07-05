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
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.ResourceUpdateMessage;
import de.fraunhofer.iais.eis.ResourceUpdateMessageBuilder;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.ids.messaging.handler.message.MessagePayloadInputstream;
import de.fraunhofer.ids.messaging.response.ErrorResponse;
import io.dataspaceconnector.services.EntityUpdateService;
import io.dataspaceconnector.services.resources.SubscriberNotificationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.ReflectionTestUtils;

import javax.xml.datatype.DatatypeFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ResourceUpdateMessageHandlerTest {

    @SpyBean
    EntityUpdateService updateService;

    @MockBean
    SubscriberNotificationService notificationService;

    @Autowired
    ResourceUpdateMessageHandler handler;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(handler, "notificationService", notificationService);
    }

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
    public void handleMessage_nullMessage_returnVersionNotSupported() {
        /* ARRANGE */
        final var message = getResourceUpdateMessageWithInvalidVersion();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage((ResourceUpdateMessageImpl) message, null);

        /* ASSERT */
        assertEquals(RejectionReason.VERSION_NOT_SUPPORTED, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_missingAffectedResource_returnBadRequestResponseMessage() {
        /* ARRANGE */
        final var message = getResourceUpdateMessage();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage((ResourceUpdateMessageImpl) message, null);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_nullPayload_returnBadRequestResponseMessage() {
        /* ARRANGE */
        final var message = getResourceUpdateMessage();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage((ResourceUpdateMessageImpl) message, null);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_illPayload_returnBadRequestResponseMessage() {
        /* ARRANGE */
        final var message = getResourceUpdateMessage();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage((ResourceUpdateMessageImpl) message,
                new MessagePayloadInputstream(null, new ObjectMapper()));

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_emptyPayload_returnBadRequestResponseMessage() {
        /* ARRANGE */
        final var message = getResourceUpdateMessage();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage(
                (ResourceUpdateMessageImpl) message, new MessagePayloadInputstream(
                InputStream.nullInputStream(), new ObjectMapper()));

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_notIdsInPayload_returnInternalRecipientErrorResponseError() {
        /* ARRANGE */
        final var message = getResourceUpdateMessage();

        final var invalidInput = "some stuff inside here";
        final InputStream stream = new ByteArrayInputStream(invalidInput.getBytes(StandardCharsets.UTF_8));

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage(
                (ResourceUpdateMessageImpl) message,
                new MessagePayloadInputstream(stream, new ObjectMapper()));

        /* ASSERT */
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_affectedResourceNotInPayload_returnBadRequestErrorResponseError()
            throws IOException {
        /* ARRANGE */
        final var message = getResourceUpdateMessage();

        final var validInput = new Serializer().serialize(new ResourceBuilder(
                URI.create("https://localhost:8080/artifacts/someOtherId"))
                .build());
        final InputStream stream = new ByteArrayInputStream(validInput.getBytes(StandardCharsets.UTF_8));

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage(
                (ResourceUpdateMessageImpl) message,
                new MessagePayloadInputstream(stream, new ObjectMapper()));

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    // @Test
    // public void handleMessage_failToUpdateResource_returnMessageProcessNotification() throws IOException {
    //     /* ARRANGE */

    //     final var message = getResourceUpdateMessage();
    //     final var validInput = new Serializer().serialize(new ResourceBuilder(URI.create("https://localhost:8080/resources/someId"))
    //                                                                 .build());
    //     final InputStream stream = new ByteArrayInputStream(validInput.getBytes(StandardCharsets.UTF_8));

    //     // Mockito.doThrow(ResourceNotFoundException.class).when(updateService).updateResource(Mockito.any());

    //     /* ACT */
    //     final var result = (BodyResponse<?>) handler.handleMessage((ResourceUpdateMessageImpl) message,
    //                                                             new MessagePayloadImpl(stream, new ObjectMapper()));

    //     /* ASSERT */
    //     assertTrue(result.getHeader() instanceof MessageProcessedNotificationMessage);
    // }


    // @Test
    // public void handleMessage_validUpdate_returnMessageProcessNotification() throws IOException {
    //     /* ARRANGE */
    //     final var message = getResourceUpdateMessage();

    //     final var artifact =new ArtifactBuilder(URI.create("https://localhost:8080/artifacts/someId")).build();
    //     final var representation = new RepresentationBuilder(URI.create("https://localhost:8080/representations/someId"))
    //             ._instance_(Util.asList(artifact))
    //             .build();
    //     final var resource = new ResourceBuilder(URI.create("https://localhost:8080/resources/someId"))
    //             ._representation_(Util.asList(representation)).build();

    //     final var validInput = new Serializer().serialize(resource);
    //     final InputStream stream = new ByteArrayInputStream(validInput.getBytes(StandardCharsets.UTF_8));

//        doNothing().when(notificationService).notifySubscribers(any());
//
//        /* ACT */
//        final var result = (BodyResponse) handler.handleMessage((ResourceUpdateMessageImpl) message,
//                                                                new MessagePayloadImpl(stream, new ObjectMapper()));
//
//        /* ASSERT TODO*/
//        // Mockito.verify(updateService).updateResource(Mockito.argThat(x -> x.getId().equals(resource.getId())));
//        // Mockito.verify(updateService).updateRepresentation(Mockito.argThat(x -> x.getId().equals(representation.getId())));
//        // Mockito.verify(updateService).updateArtifact(Mockito.argThat(x -> x.getId().equals(artifact.getId())));
//        assertTrue(result.getHeader() instanceof MessageProcessedNotificationMessage);
//        verify(notificationService, times(1)).notifySubscribers(resource.getId());
//    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    @SneakyThrows
    private ResourceUpdateMessage getResourceUpdateMessage() {
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        return new ResourceUpdateMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                ._affectedResource_(URI.create("https://localhost:8080/resources/someId"))
                .build();
    }

    @SneakyThrows
    private ResourceUpdateMessage getResourceUpdateMessageWithInvalidVersion() {
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        return new ResourceUpdateMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("tetris")
                ._issued_(xmlCalendar)
                ._affectedResource_(URI.create("https://localhost:8080/someResource"))
                .build();
    }
}
