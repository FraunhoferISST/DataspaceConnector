/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.service;

import de.fraunhofer.iais.eis.RejectionReason;
import io.dataspaceconnector.common.exception.DataRetrievalException;
import io.dataspaceconnector.common.exception.UnexpectedResponseException;
import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.service.message.builder.type.ArtifactRequestService;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Base64Utils;

import java.net.URI;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {MultipartArtifactRetriever.class})
public class MultipartArtifactRetrieverTest {

    @MockBean
    private ArtifactRequestService messageService;

    @MockBean
    private ArtifactService artifactService;

    @MockBean
    private ProducerTemplate producerTemplate;

    @MockBean
    private CamelContext camelContext;

    @Autowired
    private MultipartArtifactRetriever multipartArtifactRetriever;

    @MockBean
    private ConnectorConfig connectorConfig;

    @Test
    public void retrieve_artifactIdNull_throwIllegalArgumentException() {
        /* ARRANGE */
        final var recipient = URI.create("https://recipient.com");
        final var transferContract = URI.create("https://contract.com");

        when(artifactService.get(any())).thenCallRealMethod();

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> multipartArtifactRetriever.retrieve(
                null, recipient, transferContract, null));
    }

    @Test
    @SneakyThrows
    public void retrieve_validInput_returnData() {
        /* ARRANGE */
        final var artifactId = UUID.randomUUID();
        final var recipient = URI.create("https://recipient.com");
        final var transferContract = URI.create("https://contract.com");

        final var artifact = getArtifact();

        final var data = "DATA";
        final var response = new HashMap<String, String>();
        response.put("payload", data);

        when(artifactService.get(artifactId)).thenReturn(artifact);
        when(messageService.sendMessage(recipient, artifact.getRemoteId(), transferContract,
                null)).thenReturn(response);
        when(messageService.validateResponse(response)).thenReturn(true);

        /* ACT */
        final var result = multipartArtifactRetriever.retrieve(
                artifactId, recipient, transferContract, null);

        /* ASSERT */
        assertEquals(data, Base64Utils.encodeToString(result.readAllBytes()));
    }

    @Test
    @SneakyThrows
    public void retrieve_noValidResponse_throwDataRetrievalException() {
        /* ARRANGE */
        final var artifactId = UUID.randomUUID();
        final var recipient = URI.create("https://recipient.com");
        final var transferContract = URI.create("https://contract.com");

        final var artifact = getArtifact();

        final var data = "DATA";
        final var response = new HashMap<String, String>();
        response.put("payload", data);

        final var exception = new UnexpectedResponseException(new HashMap<>() {{
            put("reason", RejectionReason.BAD_PARAMETERS);
        }});

        when(artifactService.get(artifactId)).thenReturn(artifact);
        when(messageService.sendMessage(recipient, artifact.getRemoteId(), transferContract,
                null)).thenThrow(exception);
        when(messageService.validateResponse(response)).thenReturn(false);
        when(messageService.getResponseContent(response)).thenReturn(new HashMap<>());

        /* ACT && ASSERT */
        assertThrows(DataRetrievalException.class, () -> multipartArtifactRetriever
                .retrieve(artifactId, recipient, transferContract, null));
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private Artifact getArtifact() {
        final var artifact = new ArtifactImpl();
        ReflectionTestUtils.setField(artifact, "remoteId", URI.create("https://artifact.com"));
        return artifact;
    }
}
