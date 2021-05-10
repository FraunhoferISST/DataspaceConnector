package io.dataspaceconnector.model.templates;

import io.dataspaceconnector.model.ArtifactDesc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.net.URI;


/**
 * Describes an artifact and all its dependencies.
 */
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
