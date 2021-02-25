package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.Set;

@Data
public class RepresentationView extends RepresentationModel<RepresentationView> {
    private String title;
    private String mediaType;
    private String language;

    private Set<EndpointId> artifacts;
}
