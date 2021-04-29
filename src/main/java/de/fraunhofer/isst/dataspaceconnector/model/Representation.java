package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.net.URI;
import java.util.List;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * A representation describes how data is presented.
 */
@Entity
@Table
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Representation extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The representation id on provider side.
     */
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

    /**
     * The artifacts associated with this representation.
     */
    @ManyToMany
    private List<Artifact> artifacts;

    /**
     * The resources associated with this representation.
     */
    @ManyToMany(mappedBy = "representations")
    private List<Resource> resources;
}
