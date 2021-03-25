package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

@Data
@EqualsAndHashCode(callSuper = false)
public class RepresentationDesc extends AbstractDescription<Representation> {

    /**
     * The representation id on provider side.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private URI remoteId;

    /**
     * The title of the representation.
     */
    private String title;

    /**
     * The media type expressed by this representation.
     */
    private String type;

    /**
     * The language used by this representation.
     */
    private String language;

    /**
     * "Standard followed at representation level, i.e. it governs the serialization of an abstract
     * content like RDF/XML."
     */
    private String standard;
}
