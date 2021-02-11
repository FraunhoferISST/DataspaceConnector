package de.fraunhofer.isst.dataspaceconnector.model.templates;

import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
import lombok.Data;

import java.util.List;

@Data
public class RepresentationTemplate {
    private RepresentationDesc desc;
    private List<ArtifactTemplate> artifacts;
}
