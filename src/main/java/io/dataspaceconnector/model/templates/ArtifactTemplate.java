package io.dataspaceconnector.model.templates;

import java.net.URI;

import io.dataspaceconnector.model.ArtifactDesc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


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
