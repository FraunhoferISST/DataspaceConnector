package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Map;
import java.util.UUID;

/**
 * A catalog groups resources.
 */
@Data
@Entity
@Table
@EqualsAndHashCode(callSuper = false)
@Setter(AccessLevel.PACKAGE)
public class Catalog extends BaseResource {

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
     * The resources grouped by the catalog.
     **/
    @MapKey(name = "id")
    @OneToMany
    private Map<UUID, OfferedResource> resources;
}
