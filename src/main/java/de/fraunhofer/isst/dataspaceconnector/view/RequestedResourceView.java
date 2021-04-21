package de.fraunhofer.isst.dataspaceconnector.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = "requested", itemRelation = "resource")
public class RequestedResourceView extends RepresentationModel<RequestedResourceView> {

    /**
     * The creation date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime creationDate;

    /**
     * The last modification date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime modificationDate;

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

    /**
     * Additional properties.
     */
    private Map<String, String> additional;
}
