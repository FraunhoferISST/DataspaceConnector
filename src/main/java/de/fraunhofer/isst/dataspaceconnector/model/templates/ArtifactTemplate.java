package de.fraunhofer.isst.dataspaceconnector.model.templates;

import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.net.URI;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class ArtifactTemplate {

    /**
     * Old remote id.
     */
    private URI oldRemoteId;

    /**
     * Artifact parameters.
     */
    private @NonNull ArtifactDesc desc;
}
