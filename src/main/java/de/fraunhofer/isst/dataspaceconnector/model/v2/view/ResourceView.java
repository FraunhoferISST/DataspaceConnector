package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Resource;
import lombok.Data;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Data
public class ResourceView implements BaseView<Resource> {
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
