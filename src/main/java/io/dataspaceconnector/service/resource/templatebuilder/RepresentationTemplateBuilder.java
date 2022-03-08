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

import java.util.stream.Collectors;

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.template.RepresentationTemplate;
import io.dataspaceconnector.service.resource.relation.RepresentationArtifactLinker;
import io.dataspaceconnector.service.resource.type.RepresentationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Builds representations from templates.
 */
@RequiredArgsConstructor
public class RepresentationTemplateBuilder {

    /**
     * Service for representations.
     */
    private final @NonNull RepresentationService representationService;

    /**
     * Links representations to artifacts.
     */
    private final @NonNull RepresentationArtifactLinker representationArtifactLinker;

    /**
     * Builds artifacts.
     */
    private final @NonNull ArtifactTemplateBuilder artifactBuilder;

    /**
     * Build a representation and dependencies from template.
     *
     * @param template The representation template.
     * @return The new representation.
     * @throws IllegalArgumentException if the passed template is null.
     */
    public Representation build(final RepresentationTemplate template) {
        Utils.requireNonNull(template, ErrorMessage.ENTITY_NULL);

        final var artifactIds =
                Utils.toStream(template.getArtifacts()).map(x -> artifactBuilder.build(x).getId())
                        .collect(Collectors.toSet());
        final var repId = representationService.identifyByRemoteId(template.getDesc()
                .getRemoteId());
        final var representation = repId.isPresent()
                ? representationService.update(repId.get(), template.getDesc())
                : representationService.create(template.getDesc());

        representationArtifactLinker.add(representation.getId(), artifactIds);

        return representation;
    }
}
