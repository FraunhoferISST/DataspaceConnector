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
package io.dataspaceconnector.service.resource.templatebuilder;

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.template.ArtifactTemplate;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Builds artifacts from templates.
 */
@RequiredArgsConstructor
public class ArtifactTemplateBuilder {

    /**
     * Service for artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Build an artifact and dependencies from a template.
     *
     * @param template The artifact template.
     * @return The new artifact.
     * @throws IllegalArgumentException if the passed template is null.
     */
    public Artifact build(final ArtifactTemplate template) {
        Utils.requireNonNull(template, ErrorMessage.ENTITY_NULL);

        final var contractId = artifactService.identifyByRemoteId(template.getDesc().getRemoteId());
        return contractId.isPresent()
                ? artifactService.update(contractId.get(), template.getDesc())
                : artifactService.create(template.getDesc());
    }
}
