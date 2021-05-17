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
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ArtifactRequestMessageBuilder;
import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayloadImpl;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ArtifactRequestHandlerTest {

    @Autowired
    ArtifactRequestHandler handler;

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

        final var message = new ArtifactRequestMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("tetris")
                ._issued_(xmlCalendar)
                ._correlationMessage_(URI.create("https://somecorrelationMessage"))
                ._requestedArtifact_(URI.create("https://someArtifact"))
                .build();

        /* ACT */
        final var result = (ErrorResponse)handler.handleMessage((ArtifactRequestMessageImpl) message, null);

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

        final var message = new ArtifactRequestMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                ._correlationMessage_(URI.create("https://somecorrelationMessage"))
                ._requestedArtifact_(URI.create("https://someArtifact"))
                .build();

        /* ACT */
        final var result = (ErrorResponse)handler.handleMessage((ArtifactRequestMessageImpl) message, null);

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

        final var message = new ArtifactRequestMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                ._correlationMessage_(URI.create("https://somecorrelationMessage"))
                ._requestedArtifact_(URI.create("https://someArtifact"))
                .build();

        /* ACT */
        final var result = (ErrorResponse)handler.handleMessage((ArtifactRequestMessageImpl) message, new MessagePayloadImpl(InputStream.nullInputStream(), new ObjectMapper()));

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }
}
