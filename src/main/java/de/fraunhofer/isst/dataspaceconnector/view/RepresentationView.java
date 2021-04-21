package de.fraunhofer.isst.dataspaceconnector.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = "representations", itemRelation = "representation")
public class RepresentationView extends RepresentationModel<RepresentationView> {
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
     * The title of the representation.
     */
    private String title;

    /**
     * The media type of the representation.
     */
    private String mediaType;

    /**
     * The language of the representation.
     */
    private String language;

    /**
     * Additional properties.
     */
    private Map<String, String> additional;
}
