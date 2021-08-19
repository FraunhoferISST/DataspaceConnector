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
package io.dataspaceconnector.common.ids.message;

import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.ids.messaging.response.ErrorResponse;
import io.dataspaceconnector.common.exception.MessageEmptyException;
import io.dataspaceconnector.common.exception.VersionNotSupportedException;
import io.dataspaceconnector.common.ids.ConnectorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {MessageResponseService.class})
class MessageResponseServiceTest {

    @MockBean
    ConnectorService connectorService;

    @Autowired
    MessageResponseService service;

    private final URI connectorId = URI.create("https://someConnectorId");
    private final String outboundVersion = "4.0.0";

    @BeforeEach
    public void init() {
        Mockito.when(connectorService.getConnectorId()).thenReturn(connectorId);
        Mockito.when(connectorService.getOutboundModelVersion()).thenReturn(outboundVersion);
    }

    /**
     * handleConnectorOfflineException
     */

    @Test
    public void handleConnectorOfflineException_nothing_temporarilyUnavailableResponse() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = service.handleConnectorOfflineException();

        /* ASSERT */
        assertTrue(result instanceof ErrorResponse);

        final var error = (ErrorResponse) result;
        assertEquals(RejectionReason.TEMPORARILY_NOT_AVAILABLE,
                error.getRejectionMessage().getRejectionReason());
        assertEquals(connectorId, error.getRejectionMessage().getIssuerConnector());
        assertEquals(outboundVersion, error.getRejectionMessage().getModelVersion());
    }

    /**
     * handleMessageEmptyException
     */

    @Test
    public void handleMessageEmptyException_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> service.handleMessageEmptyException(null));
    }

    @Test
    public void handleMessageEmptyException_validException_BadParametersResponse() {
        /* ARRANGE */
        final var exception = new MessageEmptyException("Some problem");

        /* ACT */
        final var result = service.handleMessageEmptyException(exception);

        /* ASSERT */
        assertTrue(result instanceof ErrorResponse);

        final var error = (ErrorResponse) result;
        assertEquals(RejectionReason.BAD_PARAMETERS,
                error.getRejectionMessage().getRejectionReason());
        assertEquals(exception.getMessage(), error.getErrorMessage());
        assertEquals(connectorId, error.getRejectionMessage().getIssuerConnector());
        assertEquals(outboundVersion, error.getRejectionMessage().getModelVersion());
    }

    /**
     * handleInfoModelNotSupportedException
     */

    @Test
    public void handleInfoModelNotSupportedException_nullException_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> service.handleInfoModelNotSupportedException(null, "4.0.0"));
    }

    @Test
    public void handleInfoModelNotSupportedException_nullVersion_noException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        Assertions.assertDoesNotThrow(() -> service.handleInfoModelNotSupportedException(
                new VersionNotSupportedException(""), null));
    }

    @Test
    public void handleInfoModelNotSupportedException_validInput_VersionNotSupportedResponse()
            throws IllegalAccessException, NoSuchFieldException {
        /* ARRANGE */
        final var exception = new VersionNotSupportedException("Some problem");
        final var version = "3.0.0";

        /* ACT */
        final var result = service.handleInfoModelNotSupportedException(exception, version);

        /* ASSERT */
        assertTrue(result instanceof ErrorResponse);

        final var error = (ErrorResponse) result;
        assertEquals(RejectionReason.VERSION_NOT_SUPPORTED,
                error.getRejectionMessage().getRejectionReason());
        assertEquals(exception.getMessage(), error.getErrorMessage());
        assertEquals(connectorId, error.getRejectionMessage().getIssuerConnector());
        assertEquals(outboundVersion, error.getRejectionMessage().getModelVersion());
    }

    /**
     * handleResponseMessageBuilderException
     */

    @Test
    public void handleResponseMessageBuilderException_nullException_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> service.handleResponseMessageBuilderException(null,
                        URI.create("https://someUri"), URI.create("https://someUri")));
    }


    @Test
    public void handleResponseMessageBuilderExceptionException_nullIssuer_noException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        Assertions
                .assertDoesNotThrow(() -> service.handleResponseMessageBuilderException(
                        new Exception(""), null, URI.create("https://someUri")));
    }

    @Test
    public void handleResponseMessageBuilderExceptionException_nullMessageId_noException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        Assertions.assertDoesNotThrow(() -> service.handleResponseMessageBuilderException(
                new Exception(""), URI.create("https://someUri"), null));
    }

    @Test
    public void handleResponseMessageBuilderExceptionException_validInput_RecipientResponse() {
        /* ARRANGE */
        final var exception = new Exception("Some problem");
        final var issuer = URI.create("https://someUri");
        final var messageId = URI.create("https://someUri2");

        /* ACT */
        final var result = service.handleResponseMessageBuilderException(
                exception, issuer, messageId);

        /* ASSERT */
        assertTrue(result instanceof ErrorResponse);

        final var error = (ErrorResponse) result;
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                error.getRejectionMessage().getRejectionReason());
        assertEquals("Response could not be constructed.", error.getErrorMessage());
        assertEquals(connectorId, error.getRejectionMessage().getIssuerConnector());
        assertEquals(outboundVersion, error.getRejectionMessage().getModelVersion());
    }
}
