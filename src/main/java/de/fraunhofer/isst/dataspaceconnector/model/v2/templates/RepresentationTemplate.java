package de.fraunhofer.isst.dataspaceconnector.model.v2.templates;

import de.fraunhofer.isst.dataspaceconnector.model.v2.RepresentationDesc;
import lombok.Data;

import java.util.List;

@Data
public class RepresentationTemplate {
    private RepresentationDesc desc;
    private List<ArtifactTemplate> artifacts;
}
