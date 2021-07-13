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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.controller.util.CommunicationProtocol;
import io.dataspaceconnector.exception.PolicyRestrictionException;
import io.dataspaceconnector.model.QueryInput;
import io.dataspaceconnector.service.message.type.ArtifactRequestService;
import io.dataspaceconnector.service.resource.ArtifactService;
import io.dataspaceconnector.util.ErrorMessages;
import io.dataspaceconnector.util.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

/**
 * Performs an artifact request for an artifact. All functions will block till the request is
 * completed.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class BlockingArtifactReceiver implements ArtifactRetriever {

    /**
     * Used for sending an artifact request message.
     */
    private final @NonNull ArtifactRequestService artifactReqSvc;

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
                                final URI transferContract, final CommunicationProtocol protocol) {
        return retrieve(artifactId, recipient, transferContract, protocol, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream retrieve(final UUID artifactId, final URI recipient,
                                final URI transferContract, final CommunicationProtocol protocol,
                                final QueryInput queryInput)
            throws PolicyRestrictionException {
        final var artifact = artifactService.get(artifactId);

        String data;
        if (CommunicationProtocol.IDSCP.equals(protocol)) {
            final var result = template.send("direct:artifactRequestSender",
                    ExchangeBuilder.anExchange(context)
                            .withProperty("recipient", recipient)
                            .withProperty("artifactId", artifactId)
                            .withProperty("transferContract", transferContract)
                            .withProperty("queryInput", queryInput)
                            .build());

            final var response = result.getIn().getBody(Response.class);
            data = response.getBody();
        } else {
            final var response = artifactReqSvc.sendMessage(recipient,
                    artifact.getRemoteId(), transferContract, queryInput);
            if (!artifactReqSvc.validateResponse(response)) {
                final var content = artifactReqSvc.getResponseContent(response);
                if (log.isDebugEnabled()) {
                    log.debug("Data could not be loaded. [content=({})]", content);
                }

                throw new PolicyRestrictionException(ErrorMessages.POLICY_RESTRICTION);
            }

            data = MessageUtils.extractPayloadFromMultipartMessage(response);
        }

        return new ByteArrayInputStream(Base64Utils.decodeFromString(data));
    }
}
