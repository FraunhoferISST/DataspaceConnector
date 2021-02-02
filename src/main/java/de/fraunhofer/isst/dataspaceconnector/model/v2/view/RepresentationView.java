package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Representation;
import lombok.Data;

import java.util.Set;

@Data
public class RepresentationView implements BaseView<Representation> {
    private String title;
    private String mediaType;
    private String language;

    private Set<EndpointId> artifacts;
}
