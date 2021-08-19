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

import de.fraunhofer.iais.eis.ArtifactResponseMessage;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import io.dataspaceconnector.model.message.ArtifactResponseMessageDesc;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.common.ids.DeserializationService;
import de.fraunhofer.ids.messaging.protocol.http.IdsHttpService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {ArtifactResponseService.class})
class ArtifactResponseServiceTest {

    @MockBean
    private ConnectorService connectorService;

    @MockBean
    private IdsHttpService idsHttpService;

    @MockBean
    private DeserializationService deserializationService;

    @Autowired
    private ArtifactResponseService responseService;

    @Test
    public void buildMessage_null_throwsIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> responseService.buildMessage(null));
    }

    @Test
    public void buildMessage_validDesc_returnValidMessage() {
        /* ARRANGE */
        final var desc = new ArtifactResponseMessageDesc();
        desc.setRecipient(URI.create("https://recipient"));
        desc.setCorrelationMessage(URI.create("https://correlationMessage"));
        desc.setTransferContract(URI.create("https://transferContract"));

        final var connectorId = URI.create("https://connector");
        final var modelVersion = "4.0.0";
        final var token = new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build();
        Mockito.when(connectorService.getConnectorId()).thenReturn(connectorId);
        Mockito.when(connectorService.getOutboundModelVersion()).thenReturn(modelVersion);
        Mockito.when(connectorService.getCurrentDat()).thenReturn(token);

        /* ACT */
        final var result = (ArtifactResponseMessage) responseService.buildMessage(desc);

        /* ASSERT */
        assertEquals(1, result.getRecipientConnector().size());
        assertEquals(desc.getRecipient(), result.getRecipientConnector().get(0));
        assertEquals(desc.getCorrelationMessage(), result.getCorrelationMessage());
        assertEquals(desc.getTransferContract(), result.getTransferContract());
        assertEquals(connectorId, result.getIssuerConnector());
        assertEquals(modelVersion, result.getModelVersion());
        assertEquals(token, result.getSecurityToken());
    }
}
