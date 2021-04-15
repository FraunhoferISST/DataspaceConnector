package de.fraunhofer.isst.dataspaceconnector.services.ids;

import java.math.BigInteger;
import java.net.URI;

import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public class IdsArtifactBuilder extends AbstractIdsBuilder<Artifact, de.fraunhofer.iais.eis.Artifact> {

    @Override
    protected de.fraunhofer.iais.eis.Artifact createInternal(final Artifact artifact,
                                                             final URI baseUri,
                                                             final int currentDepth,
                                                             final int maxDepth) {
        // Prepare artifact attributes.
        final var created = IdsUtils.getGregorianOf(artifact
                                                            .getCreationDate());

        return new ArtifactBuilder(getAbsoluteSelfLink(artifact, baseUri))
                ._byteSize_(BigInteger.ONE) // TODO get the real file size (how?)
                ._creationDate_(created)
                ._fileName_(artifact.getTitle())
                .build();
    }
}
