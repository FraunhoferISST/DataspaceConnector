package de.fraunhofer.isst.dataspaceconnector.model.templates;

import java.net.URI;
import java.util.List;

import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
