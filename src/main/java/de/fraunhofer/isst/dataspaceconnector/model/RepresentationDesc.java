package de.fraunhofer.isst.dataspaceconnector.model;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Describes a representation. Use this for creating or updating a representation.
 */
@Data
@EqualsAndHashCode(callSuper = true)
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
    private String mediaType;

    /**
     * The language used by this representation.
     */
    private String language;

    /**
     * "Standard followed at representation level, i.e. it governs
     * the serialization of an abstract content like RDF/XML."
     */
    private String standard;
}
