package io.dataspaceconnector.model;

import java.net.URI;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Describes resource requested by this connector.
 */
@Entity
@SQLDelete(sql = "UPDATE resource SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
public final class RequestedResource extends Resource {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The resource id on provider side.
     */
    private URI remoteId;

    /**
     * Default constructor.
     */
    protected RequestedResource() {
        super();
    }

    /**
     * List of backends subscribed to resource updates.
     */
    @ElementCollection
    @JsonProperty("subscribers")
    private List<URI> subscribers;

    /**
     * The catalogs in which this resource is used.
     */
    @ManyToMany(mappedBy = "requestedResources")
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

    /**
     * Sets the list of subscribers for this resource.
     *
     * @param subscriberList the list of subscribers.
     */
    public void setSubscribers(final List<URI> subscriberList) {
        this.subscribers = subscriberList;
    }
}
