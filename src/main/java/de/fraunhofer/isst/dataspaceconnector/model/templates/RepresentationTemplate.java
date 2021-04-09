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

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class RepresentationTemplate {
    private          URI                oldRemoteId;
    @Setter(AccessLevel.NONE)
    private @NonNull RepresentationDesc desc;
    private          List<ArtifactTemplate> artifacts;
}
