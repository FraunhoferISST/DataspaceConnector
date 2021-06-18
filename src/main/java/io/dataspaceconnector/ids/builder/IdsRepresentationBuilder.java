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
package io.dataspaceconnector.ids.builder;

import java.net.URI;
import java.util.List;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.IANAMediaTypeBuilder;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.RepresentationInstance;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.common.BasePath;
import io.dataspaceconnector.ids.builder.core.base.AbstractIdsBuilder;
import io.dataspaceconnector.ids.util.IdsUtils;
import io.dataspaceconnector.model.core.Representation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Converts DSC representation to ids representation.
 */
@Component
@RequiredArgsConstructor
public final class IdsRepresentationBuilder
        extends AbstractIdsBuilder<Representation, de.fraunhofer.iais.eis.Representation> {

    /**
     * The builder for Infomodel Artifacts.
     */
    private final @NonNull IdsArtifactBuilder artifactBuilder;

    @Override
    protected de.fraunhofer.iais.eis.Representation createInternal(
            final Representation representation, final int currentDepth,
            final int maxDepth) throws ConstraintViolationException {
        // Build children.
        final var artifacts =
                create(artifactBuilder, representation.getArtifacts(), currentDepth, maxDepth);

        // Build representation only if at least one artifact is present.
        if (artifacts.isEmpty() || artifacts.get().isEmpty()) {
            return null;
        }

        // Prepare representation attributes.
        final var modified = IdsUtils.getGregorianOf(representation
                .getModificationDate());
        final var created = IdsUtils.getGregorianOf(representation
                .getCreationDate());
        final var language = IdsUtils.getLanguage(representation.getLanguage());
        final var mediaType =
                new IANAMediaTypeBuilder()._filenameExtension_(representation.getMediaType())
                        .build();
        final var standard = URI.create(representation.getStandard());

        final var builder = new RepresentationBuilder(URI.create(
                BasePath.REPRESENTATIONS + "/" + representation.getId()))
                ._created_(created)
                ._language_(language)
                ._mediaType_(mediaType)
                ._modified_(modified)
                ._representationStandard_(standard);

        if (artifacts.isPresent()) {
           builder._instance_(getArtifacts(artifacts.get()));
        }

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private List<RepresentationInstance> getArtifacts(final List<Artifact> artifacts) {
        return (List<RepresentationInstance>) (List<?>) artifacts;
    }
}
