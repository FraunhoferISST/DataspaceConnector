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
package io.dataspaceconnector.service.message.builder;

import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.model.message.ArtifactRequestMessageDesc;
import io.dataspaceconnector.service.message.builder.base.IdsMessageBuilder;
import io.dataspaceconnector.service.message.builder.type.ArtifactRequestService;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Builds an ArtifactRequestMessage and creates a request DTO with header and payload.
 */
@Component("ArtifactRequestMessageBuilder")
@RequiredArgsConstructor
public class ArtifactRequestMessageBuilder
        extends IdsMessageBuilder<ArtifactRequestMessageImpl, QueryInput> {

    /**
     * Service for artifact request message handling.
     */
    private final @NonNull ArtifactRequestService artifactReqSvc;

    /**
     * Builds an ArtifactRequestMessage according to the exchange properties and creates a Request
     * with the message as header and an empty payload.
     *
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    @Override
    protected Request<ArtifactRequestMessageImpl, QueryInput, Optional<Jws<Claims>>>
    processInternal(final Exchange exchange) {
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);
        final var agreementId = exchange
                .getProperty(ParameterUtils.TRANSFER_CONTRACT_PARAM, URI.class);
        final var queryInput = exchange
                .getProperty(ParameterUtils.QUERY_INPUT_PARAM, QueryInput.class);

        URI artifactId = exchange.getProperty(ParameterUtils.ARTIFACT_ID_PARAM, URI.class);
        if (artifactId == null) {
            final var index = exchange.getProperty(Exchange.LOOP_INDEX, Integer.class);
            final var artifacts = exchange
                    .getProperty(ParameterUtils.ARTIFACTS_PARAM, List.class);
            artifactId = (URI) artifacts.get(index);
        }

        final var message = (ArtifactRequestMessageImpl) artifactReqSvc
                .buildMessage(new ArtifactRequestMessageDesc(recipient, artifactId, agreementId));

        return new Request<>(message, queryInput, Optional.empty());
    }

}
