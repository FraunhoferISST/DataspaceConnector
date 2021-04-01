package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.ElementCollection;
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
import org.springframework.data.annotation.Version;

/**
 * A resource describes offered or requested data.
 */
@Entity
@Inheritance
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Resource extends AbstractEntity {
    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

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
    @ElementCollection
    private List<String> keywords;

    /**
     * The publisher of the resource.
     */
    private URI publisher;

    /**
     * The owner of the resource.
     */
    private URI sovereign;

    /**
     * The language of the resource.
     */
    private String language;

    /**
     * The licence of the resource.
     */
    private URI licence;

    /**
     * The endpoint of the resource.
     */
    private URI endpointDocumentation;

    /**
     * The version of the resource.
     */
    @Version
    private long version;

    /**
     * The representation available for the resource.
     */
    @ManyToMany
    private List<Representation> representations;

    /**
     * The contracts available for the resource.
     */
    @ManyToMany
    private List<Contract> contracts;

    public void setCatalogs(final List<Catalog> catalogs) { }

    public List<Catalog> getCatalogs() { return null; }
}
