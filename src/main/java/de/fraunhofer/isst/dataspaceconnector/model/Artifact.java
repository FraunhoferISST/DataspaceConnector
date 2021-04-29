package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.ManyToMany;
import java.net.URI;
import java.util.List;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * An artifact stores and encapsulates data.
 */
@Inheritance
@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
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
     * The provider's address for artifact request messages.
     */
    private URI remoteAddress;

    /**
     * The title of the catalog.
     **/
    private String title;

    /**
     * The counter of how often the underlying data has been accessed.
     */
    private long numAccessed;

    /**
     * Indicates whether the artifact should be downloaded automatically.
     */
    private boolean automatedDownload;

    /**
     * The byte size of the artifact.
     */
    private long byteSize;

    /**
     * The CRC32C CheckSum of the artifact.
     */
    private long checkSum;

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

    /**
     * The agreements that refer to this artifact.
     */
    @ManyToMany(mappedBy = "artifacts")
    private List<Agreement> agreements;
}
