package de.fraunhofer.isst.dataspaceconnector.model.view;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Setter
@Relation(collectionRelation = "requested", itemRelation = "resource")
public class RequestedResourceView extends RepresentationModel<RequestedResourceView> {

    private Date creationDate;
    private Date modificationDate;

    /**
     * The title of the resource.
     */
    private String title;

    /**
     * The description of the resource.
     */
    private String description;

    /**
     * The keywords of the resource.
     */
    private List<String> keywords;

    /**
     * The publisher of the resource.
     */
    private URI publisher;

    /**
     * The language of the resource.
     */
    private String language;

    /**
     * The licence of the resource.
     */
    private URI licence;

    /**
     * The version of the resource.
     */
    private long version;

    /**
     * The owner of the resource.
     */
    private URI sovereign;

    /**
     * The endpoint of the resource.
     */
    private URI endpointDocumentation;

    private Map<String, String> additional;
}
