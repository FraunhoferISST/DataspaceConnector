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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

/**
 * Performs an artifact request for an artifact via IDSCP2. All functions will block till the
 * request is completed.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "idscp2", value = "enabled", havingValue = "true")
public class Idscp2ArtifactRetriever implements ArtifactRetriever {

    /**
     * Used for accessing artifacts and their data.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Template for triggering Camel routes.
     */
    private final @NonNull ProducerTemplate template;

    /**
     * The CamelContext required for constructing the {@link ProducerTemplate}.
     */
    private final @NonNull CamelContext context;

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream retrieve(final UUID artifactId, final URI recipient,
                                final URI transferContract) {
        return retrieve(artifactId, recipient, transferContract, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream retrieve(final UUID artifactId, final URI recipient,
                                final URI transferContract, final QueryInput queryInput) {
        final var artifact = artifactService.get(artifactId);
        final var result = template.send("direct:artifactRequestSender",
                ExchangeBuilder.anExchange(context)
                        .withProperty(ParameterUtils.RECIPIENT_PARAM, recipient)
                        .withProperty(ParameterUtils.ARTIFACT_ID_PARAM, artifact.getRemoteId())
                        .withProperty(ParameterUtils.TRANSFER_CONTRACT_PARAM, transferContract)
                        .withProperty(ParameterUtils.QUERY_INPUT_PARAM, queryInput)
                        .build());

        final var response = result.getIn().getBody(Response.class);
        final var data = response.getBody();

        return new ByteArrayInputStream(Base64Utils.decodeFromString(data));
    }

}
