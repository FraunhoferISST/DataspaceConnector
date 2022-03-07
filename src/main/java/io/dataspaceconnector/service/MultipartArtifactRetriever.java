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
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.ids.message.MessageUtils;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.exception.DataRetrievalException;
import io.dataspaceconnector.common.exception.PolicyRestrictionException;
import io.dataspaceconnector.common.exception.UnexpectedResponseException;
import io.dataspaceconnector.service.message.builder.type.ArtifactRequestService;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

/**
 * Performs an artifact request for an artifact via Multipart. All functions will block till the
 * request is completed.
 */
@Component
@Log4j2
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "idscp2", value = "enabled", havingValue = "false",
        matchIfMissing = true)
public class MultipartArtifactRetriever implements ArtifactRetriever {

    /**
     * Used for sending an artifact request message.
     */
    private final @NonNull ArtifactRequestService artifactReqSvc;

    /**
     * Used for accessing artifacts and their data.
     */
    private final @NonNull ArtifactService artifactService;

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
                                final URI transferContract,
                                final QueryInput queryInput)
            throws PolicyRestrictionException {
        final var artifact = artifactService.get(artifactId);

        Map<String, String> response;
        try {
            response = artifactReqSvc.sendMessage(recipient,
                    artifact.getRemoteId(), transferContract, queryInput);
        } catch (UnexpectedResponseException exception) {
            final var content = exception.getContent();
            if (log.isDebugEnabled()) {
                log.debug("Data could not be loaded. [content=({})]", content);
            }

        if (content.containsKey("reason")) {
            final var reason = content.get("reason");
            if (reason.equals(RejectionReason.NOT_AUTHORIZED)) {
                throw new PolicyRestrictionException(ErrorMessage.POLICY_RESTRICTION);
            }
        }

            throw new DataRetrievalException(content.toString());
        }

        final var data = MessageUtils.extractPayloadFromMultipartMessage(response);

        return new ByteArrayInputStream(Base64Utils.decodeFromString(data));
    }
}
