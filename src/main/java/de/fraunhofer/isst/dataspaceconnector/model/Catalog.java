package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * A catalog groups resources.
 */
@Data
@Entity
@Table
@EqualsAndHashCode(callSuper = false)
@Setter(AccessLevel.PACKAGE)
public class Catalog extends AbstractEntity {
    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The title of the catalog.
     **/
    private String title;

    /**
     * The description of the catalog.
     **/
    private String description;

    /**
     * The offered resources grouped by the catalog.
     **/
    @OneToMany
    @RestResource
    private List<OfferedResource> offeredResources;

    /**
     * The requested resources grouped by the catalog.
     **/
    @OneToMany
    @RestResource
    private List<RequestedResource> requestedResources;
}
