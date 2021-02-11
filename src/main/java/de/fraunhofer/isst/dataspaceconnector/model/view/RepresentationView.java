package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import lombok.Data;

import java.util.Set;

@Data
public class RepresentationView implements BaseView<Representation> {
    private String title;
    private String mediaType;
    private String language;

    private Set<EndpointId> artifacts;
}
