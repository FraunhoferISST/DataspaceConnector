package de.fraunhofer.isst.dataspaceconnector.model.templates;

import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.URI;
import java.util.List;

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
