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
package io.dataspaceconnector.service.usagecontrol;

import io.dataspaceconnector.service.resource.type.ArtifactService;
import io.dataspaceconnector.common.net.EndpointUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.ZonedDateTime;

/**
 * This class provides access permission information for the {@link RuleValidator} depending on
 * the policy content.
 */
@Service
@RequiredArgsConstructor
public class PolicyInformationService {

    /**
     * Service for handling artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Get creation date of artifact.
     *
     * @param target The target id.
     * @return The artifact's creation date.
     */
    public ZonedDateTime getCreationDate(final URI target) {
        final var resourceId = EndpointUtils.getUUIDFromPath(target);
        final var artifact = artifactService.get(resourceId);

        return artifact.getCreationDate();
    }

    /**
     * Get access number of artifact.
     *
     * @param target The target id.
     * @return The artifact's access number.
     */
    public long getAccessNumber(final URI target) {
        final var resourceId = EndpointUtils.getUUIDFromPath(target);
        final var artifact = artifactService.get(resourceId);

        return artifact.getNumAccessed();
    }
}
