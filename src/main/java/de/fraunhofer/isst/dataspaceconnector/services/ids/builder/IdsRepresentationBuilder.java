package de.fraunhofer.isst.dataspaceconnector.services.ids.builder;

import java.net.URI;

import de.fraunhofer.iais.eis.IANAMediaTypeBuilder;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
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
            final Representation representation,
            final URI baseUri, final int currentDepth,
            final int maxDepth) throws ConstraintViolationException {
        // Build children.
        final var artifacts =
                create(artifactBuilder, representation.getArtifacts(), baseUri, currentDepth,
                        maxDepth);

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
