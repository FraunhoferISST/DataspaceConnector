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
package io.dataspaceconnector.services;

import io.dataspaceconnector.exceptions.PolicyRestrictionException;
import io.dataspaceconnector.model.QueryInput;
import io.dataspaceconnector.services.messages.types.ArtifactRequestService;
import io.dataspaceconnector.services.resources.ArtifactService;
import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

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
    private final @NonNull ArtifactRequestService messageService;

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
                                final URI transferContract, final QueryInput queryInput)
            throws PolicyRestrictionException {
        final var artifact = artifactService.get(artifactId);
        final var response = messageService.sendMessage(recipient,
                artifact.getRemoteId(), transferContract, queryInput);
        if (!messageService.validateResponse(response)) {
            final var content = messageService.getResponseContent(response);
            if (log.isDebugEnabled()) {
                log.debug("Data could not be loaded. [content=({})]", content);
            }

            throw new PolicyRestrictionException(ErrorMessages.POLICY_RESTRICTION);
        }

        final var data = MessageUtils.extractPayloadFromMultipartMessage(response);
        return new ByteArrayInputStream(Base64Utils.decodeFromString(data));
    }
}
