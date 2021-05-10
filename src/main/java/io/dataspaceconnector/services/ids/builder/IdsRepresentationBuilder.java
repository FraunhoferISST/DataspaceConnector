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
package io.dataspaceconnector.services.ids.builder;

import de.fraunhofer.iais.eis.IANAMediaTypeBuilder;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.model.Representation;
import io.dataspaceconnector.utils.IdsUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;

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
            final Representation representation,
            final URI baseUri, final int currentDepth,
            final int maxDepth) throws ConstraintViolationException {
        // Build children.
        final var artifacts =
                create(artifactBuilder, representation.getArtifacts(), baseUri, currentDepth,
                        maxDepth);

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

        final var builder = new RepresentationBuilder(getAbsoluteSelfLink(representation, baseUri))
                ._created_(created)
                ._language_(language)
                ._mediaType_(mediaType)
                ._modified_(modified)
                ._representationStandard_(standard);

        artifacts.ifPresent(builder::_instance_);

        return builder.build();
    }
}
