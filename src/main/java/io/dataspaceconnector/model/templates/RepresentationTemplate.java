package io.dataspaceconnector.model.templates;

import io.dataspaceconnector.model.RepresentationDesc;
import lombok.*;

import java.net.URI;
import java.util.List;

/**
 * Describes a representation and all its dependencies.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class RepresentationTemplate {

    /**
     * Old remote id.
     */
    private URI oldRemoteId;

    /**
     * Representation parameters.
     */
    @Setter(AccessLevel.NONE)
    private @NonNull RepresentationDesc desc;

    /**
     * List of artifact templates.
     */
    private List<ArtifactTemplate> artifacts;
}
