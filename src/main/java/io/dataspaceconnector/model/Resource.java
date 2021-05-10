package io.dataspaceconnector.model;

import io.dataspaceconnector.exceptions.ResourceException;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.Version;

import javax.persistence.*;
import java.net.URI;
import java.util.List;

/**
 * A resource describes offered or requested data.
 */
@Entity
@Inheritance
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE resource SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Table(name = "resource")
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

    /**
     * Set the catalogs used by this resource.
     * @param catalogList The catalog list.
     */
    public void setCatalogs(final List<Catalog> catalogList) {
        /*
            NOTE: Offered and Requested Resource override this function.
         */
        throw new ResourceException("Not implemented");
    }

    /**
     * Get the list of catalogs used by this resource.
     * @return The list of catalogs used by this resource.
     */
    public List<Catalog> getCatalogs() {
        /*
            NOTE: Offered and Requested Resource override this function
            so that null should never be returned. Return null here so
            that a missing override crashes really load.
         */
        return null;
    }
}
