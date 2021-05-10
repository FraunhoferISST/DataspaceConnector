package io.dataspaceconnector.services.ids.builder;

import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.model.Artifact;
import io.dataspaceconnector.utils.IdsUtils;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.URI;

/**
 * Converts DSC Artifacts to Infomodel Artifacts.
 */
@Component
@NoArgsConstructor
public final class IdsArtifactBuilder
        extends AbstractIdsBuilder<Artifact, de.fraunhofer.iais.eis.Artifact> {

    @Override
    protected de.fraunhofer.iais.eis.Artifact createInternal(final Artifact artifact,
                                                             final URI baseUri,
                                                             final int currentDepth,
                                                             final int maxDepth)
            throws ConstraintViolationException {
        // Prepare artifact attributes.
        final var created = IdsUtils.getGregorianOf(artifact
                                                            .getCreationDate());

        return new ArtifactBuilder(getAbsoluteSelfLink(artifact, baseUri))
                ._byteSize_(BigInteger.valueOf(artifact.getByteSize()))
                ._checkSum_(BigInteger.valueOf(artifact.getCheckSum()).toString())
                ._creationDate_(created)
                ._fileName_(artifact.getTitle())
                .build();
    }
}
