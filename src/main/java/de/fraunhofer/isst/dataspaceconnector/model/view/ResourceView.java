package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Data
public class ResourceView<T extends Resource> extends RepresentationModel<ResourceView<T>> {
    private String title;
    private String description;
    private List<String> keywords;
    private URI publisher;
    private String language;
    private URI licence;

    private long version;

    private Set<EndpointId> representations;
    private Set<EndpointId> contracts;
}
