package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Resource;
import lombok.Data;

import java.util.Set;

@Data
public class ResourceView implements BaseView<Resource> {
    private String title;
    private String description;
    private String keywords;
    private String publisher;
    private String language;
    private String licence;

    private long version;

    private Set<EndpointId> representations;
    private Set<EndpointId> contracts;
}
