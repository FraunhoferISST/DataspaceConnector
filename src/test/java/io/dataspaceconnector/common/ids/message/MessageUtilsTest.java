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

import de.fraunhofer.iais.eis.ArtifactRequestMessage;
import de.fraunhofer.iais.eis.ArtifactRequestMessageBuilder;
import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RejectionMessageBuilder;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.ResourceUpdateMessage;
import de.fraunhofer.iais.eis.ResourceUpdateMessageBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.common.exception.MessageEmptyException;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

import static de.fraunhofer.ids.messaging.util.IdsMessageUtils.getGregorianNow;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageUtilsTest {

    final static URI messageId = URI.create("https://messageId");
    final static URI connectorId = URI.create("https://connectorId");
    final static URI requestedElement = URI.create("https://requestedElement");
    final static URI requestedArtifact = URI.create("https://requestedArtifact");
    final static URI affectedResource = URI.create("https://affectedResource");
    final static URI transferContract = URI.create("https://transferContract");
    final static URI correlationMessage = URI.create("https://correlationMessage");
    final static String modelVersion = "4.0.0";
    final static DynamicAttributeToken token = new DynamicAttributeTokenBuilder()
            ._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build();

    @Test
    public void extractRequestedElement_messageWithRequestedElement_returnRequestedElement() {
        /* ARRANGE */
        final var message = getDescriptionRequestMessageWithRequestedElement();

        /* ACT */
        final var result = MessageUtils.extractRequestedElement(message);

        /* ASSERT */
        assertEquals(requestedElement, result);
    }

    @Test
    public void extractRequestedElement_messageWithoutRequestedElement_returnNull() {
        /* ARRANGE */
        final var message = getDescriptionRequestMessageWithoutRequestedElement();

        /* ACT */
        final var result = MessageUtils.extractRequestedElement(message);

        /* ASSERT */
        assertNull(result);
    }

    @Test
    public void extractRequestedElement_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MessageUtils.extractRequestedElement(null));
    }

    @Test
    public void extractRequestedArtifact_messageWithRequestedArtifact_returnRequestedArtifact() {
        /* ARRANGE */
        final var message = getArtifactRequestMessageWithoutTransferContract();

        /* ACT */
        final var result = MessageUtils.extractRequestedArtifact(message);

        /* ASSERT */
        assertEquals(requestedArtifact, result);
    }

    @Test
    public void extractRequestedArtifact_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MessageUtils.extractRequestedElement(null));
    }

    @Test
    public void extractTransferContract_messageWithTransferContract_returnTransferContract() {
        /* ARRANGE */
        final var message = getArtifactRequestMessageWithTransferContract();

        /* ACT */
        final var result = MessageUtils.extractTransferContract(message);

        /* ASSERT */
        assertEquals(transferContract, result);
    }

    @Test
    public void extractTransferContract_messageWithoutTransferContract_returnNull() {
        /* ARRANGE */
        final var message = getArtifactRequestMessageWithoutTransferContract();

        /* ACT */
        final var result = MessageUtils.extractTransferContract(message);

        /* ASSERT */
        assertNull(result);
    }

    @Test
    public void extractTransferContract_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MessageUtils.extractTransferContract(null));
    }

    @Test
    public void extractAffectedResource_messageWithAffectedResource_returnAffectedResource() {
        /* ARRANGE */
        final var message = getResourceUpdateMessageWithAffectedResource();

        /* ACT */
        final var result = MessageUtils.extractAffectedResource(message);

        /* ASSERT */
        assertEquals(affectedResource, result);
    }

    @Test
    public void extractAffectedResource_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MessageUtils.extractAffectedResource(null));
    }

    @Test
    public void extractIssuerConnector_validMessage_returnIssuerConnector() {
        /* ARRANGE */
        final var message = getDescriptionRequestMessageWithRequestedElement();

        /* ACT */
        final var result = MessageUtils.extractIssuerConnector(message);

        /* ASSERT */
        assertEquals(connectorId, result);
    }

    @Test
    public void extractIssuerConnector_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MessageUtils.extractIssuerConnector(null));
    }

    @Test
    public void extractMessageId_validMessage_returnIssuerConnector() {
        /* ARRANGE */
        final var message = getDescriptionRequestMessageWithRequestedElement();

        /* ACT */
        final var result = MessageUtils.extractMessageId(message);

        /* ASSERT */
        assertEquals(messageId, result);
    }

    @Test
    public void extractMessageId_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MessageUtils.extractMessageId(null));
    }

    @Test
    public void extractModelVersion_validMessage_returnIssuerConnector() {
        /* ARRANGE */
        final var message = getDescriptionRequestMessageWithRequestedElement();

        /* ACT */
        final var result = MessageUtils.extractModelVersion(message);

        /* ASSERT */
        assertEquals(modelVersion, result);
    }

    @Test
    public void extractModelVersion_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MessageUtils.extractModelVersion(null));
    }

    @Test
    public void extractRejectionReason_validRejectionMessage_returnRejectionReason() {
        /* ARRANGE */
        final var rejectionReason = RejectionReason.NOT_FOUND;
        final var message = getRejectionMessage(rejectionReason);

        /* ACT */
        final var result = MessageUtils.extractRejectionReason(message);

        /* ASSERT */
        assertEquals(rejectionReason, result);
    }

    @Test
    public void extractRejectionReason_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MessageUtils.extractRejectionReason(null));
    }

    @Test
    public void checkForEmptyMessage_null_throwMessageEmptyException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(MessageEmptyException.class, () -> MessageUtils.checkForEmptyMessage(null));
    }

    @Test
    public void checkForEmptyMessage_validMessage_nothing() {
        /* ARRANGE */
        final var message = getDescriptionRequestMessageWithRequestedElement();

        /* ACT & ASSERT */
        assertDoesNotThrow(() -> MessageUtils.checkForEmptyMessage(message));
    }

    @Test
    public void checkForVersionSupport_validVersion_nothing() {
        /* ARRANGE */
        final var inboundModelVersions = List.of("4.0.0", "4.0.1");

        /* ACT & ASSERT */
        assertDoesNotThrow(() -> MessageUtils.checkForVersionSupport(modelVersion, inboundModelVersions));
    }

    @Test
    public void extractHeaderFromMultipartMessage_mapWithHeaderValue_returnHeaderValue() {
        /* ARRANGE */
        final var headerValue = "some header values";
        final var response = new HashMap<String, String>();
        response.put("header", headerValue);
        response.put("payload", "some payload values");

        /* ACT */
        final var result = MessageUtils.extractHeaderFromMultipartMessage(response);

        /* ASSERT */
        assertEquals(headerValue, result);
    }

    @Test
    public void extractHeaderFromMultipartMessage_mapWithoutHeaderValue_returnNull() {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("payload", "some payload values");

        /* ACT */
        final var result = MessageUtils.extractHeaderFromMultipartMessage(response);

        /* ACT & ASSERT */
        assertNull(result);
    }

    @Test
    public void extractHeaderFromMultipartMessage_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MessageUtils.extractHeaderFromMultipartMessage(null));
    }

    @Test
    public void extractPayloadFromMultipartMessage_mapWithPayloadValue_returnPayloadValue() {
        /* ARRANGE */
        final var payloadValue = "some payload values";
        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("payload", payloadValue);

        /* ACT */
        final var result = MessageUtils.extractPayloadFromMultipartMessage(response);

        /* ASSERT */
        assertEquals(payloadValue, result);
    }

    @Test
    public void extractPayloadFromMultipartMessage_mapWithoutPayloadValue_returnNull() {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("header", "some header values");

        /* ACT */
        final var result = MessageUtils.extractPayloadFromMultipartMessage(response);

        /* ACT & ASSERT */
        assertNull(result);
    }

    @Test
    public void extractPayloadFromMultipartMessage_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MessageUtils.extractPayloadFromMultipartMessage(null));
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private DescriptionRequestMessage getDescriptionRequestMessageWithRequestedElement() {
        return new DescriptionRequestMessageBuilder(messageId)
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._requestedElement_(requestedElement)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(connectorId))
                .build();
    }

    private DescriptionRequestMessage getDescriptionRequestMessageWithoutRequestedElement() {
        return new DescriptionRequestMessageBuilder(messageId)
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(connectorId))
                .build();
    }

    private ArtifactRequestMessage getArtifactRequestMessageWithTransferContract() {
        return new ArtifactRequestMessageBuilder(messageId)
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._requestedArtifact_(requestedArtifact)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(connectorId))
                ._transferContract_(transferContract)
                .build();
    }

    private ArtifactRequestMessage getArtifactRequestMessageWithoutTransferContract() {
        return new ArtifactRequestMessageBuilder(messageId)
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._requestedArtifact_(requestedArtifact)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(connectorId))
                .build();
    }

    private RejectionMessage getRejectionMessage(final RejectionReason rejectionReason) {
        return new RejectionMessageBuilder(messageId)
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(connectorId))
                ._rejectionReason_(rejectionReason)
                ._correlationMessage_(correlationMessage)
                .build();
    }

    private ResourceUpdateMessage getResourceUpdateMessageWithAffectedResource() {
        return new ResourceUpdateMessageBuilder(messageId)
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(connectorId))
                ._affectedResource_(affectedResource)
                .build();
    }
}
