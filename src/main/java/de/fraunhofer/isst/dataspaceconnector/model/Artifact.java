package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.ManyToMany;
import java.net.URI;
import java.util.List;

/**
 * An artifact stores and encapsulates data.
 */
@Getter
@RequiredArgsConstructor
@Inheritance
@Entity
@EqualsAndHashCode(callSuper = false)
@Setter(AccessLevel.PACKAGE)
public abstract class Artifact extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The artifact id on provider side.
     */
    private URI remoteId;

    /**
     * The title of the catalog.
     **/
    private String title;

    /**
     * The counter of how often the underlying data has been accessed.
     */
    private Long numAccessed;

    /**
     * Increment the data access counter.
     */
    public void incrementAccessCounter() {
        numAccessed += 1;
    }

    /**
     * The representations in which this artifact is used.
     */
    @ManyToMany(mappedBy = "artifacts")
    private List<Representation> representations;
}
