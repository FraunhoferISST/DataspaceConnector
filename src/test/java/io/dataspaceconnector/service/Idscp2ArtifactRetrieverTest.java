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
package io.dataspaceconnector.service;

import java.net.URI;
import java.util.UUID;

import de.fraunhofer.iais.eis.ArtifactResponseMessage;
import de.fraunhofer.iais.eis.ArtifactResponseMessageBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Base64Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Idscp2ArtifactRetrieverTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @Mock
    private ExtendedCamelContext camelContext;

    @Mock
    private ProducerTemplate producerTemplate;

    @Mock
    private ArtifactService artifactService;

    private Idscp2ArtifactRetriever retriever;

    final URI uri = URI.create("https://uri");

    @BeforeEach
    void init() {
        retriever = new Idscp2ArtifactRetriever(artifactService,
                producerTemplate, camelContext);
    }

    @Test
    @SneakyThrows
    void retrieve_noExceptionInRoute_returnData() {
        /* ARRANGE */
        final var artifact = new ArtifactImpl();
        final var data = "data";
        final var response = new Response(getHeader(), data);

        when(artifactService.get(any())).thenReturn(artifact);
        when(producerTemplate.send(anyString(), any(Exchange.class))).thenReturn(exchange);
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(Response.class)).thenReturn(response);

        /* ACT */
        final var result = retriever.retrieve(UUID.randomUUID(), uri, uri);

        /* ASSERT */
        assertEquals(data, Base64Utils.encodeToString(result.readAllBytes()));
    }

    private ArtifactResponseMessage getHeader() {
        return new ArtifactResponseMessageBuilder()
                ._securityToken_(new DynamicAttributeTokenBuilder()
                        ._tokenFormat_(TokenFormat.JWT)
                        ._tokenValue_("token")
                        .build())
                ._correlationMessage_(uri)
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._issuerConnector_(uri)
                ._modelVersion_("4.0.0")
                ._senderAgent_(uri)
                ._recipientConnector_(Util.asList(uri))
                ._transferContract_(uri)
                .build();
    }

}
