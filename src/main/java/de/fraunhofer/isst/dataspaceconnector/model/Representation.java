package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * A representation describes how data is presented.
 */
@Data
@Entity
@Table
@EqualsAndHashCode(callSuper = false)
@Setter(AccessLevel.PACKAGE)
public class Representation extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

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
     * The artifacts associated with this representation.
     **/
    @ManyToMany
    private List<Artifact> artifacts;
}
