package io.dataspaceconnector.model;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.net.URI;
import java.util.List;

/**
 * A representation describes how data is presented.
 */
@Entity
@Table(name = "representation")
@SQLDelete(sql = "UPDATE representation SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
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
