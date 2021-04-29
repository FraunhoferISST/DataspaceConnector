package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Describes resources offered by this connector.
 */
@Entity
@Table
@EqualsAndHashCode(callSuper = true)
public final class OfferedResource extends Resource {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    protected OfferedResource() {
        super();
    }

    /**
     * The catalogs in which this resource is used.
     */
    @ManyToMany(mappedBy = "offeredResources")
    private List<Catalog> catalogs;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCatalogs(final List<Catalog> catalogList) {
        this.catalogs = catalogList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Catalog> getCatalogs() {
        return catalogs;
    }
}
