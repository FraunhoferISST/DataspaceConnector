package de.fraunhofer.isst.dataspaceconnector.model.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * An artifact stores and encapsulates data.
 */
@lombok.Data
@Entity
@Table
@EqualsAndHashCode(callSuper = false)
@Setter(AccessLevel.PACKAGE)
public class Artifact extends BaseResource {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The title of the catalog.
     **/
    private String title;

    /**
     * The data stored in the artifact.
     **/
    @OneToOne(cascade = CascadeType.REMOVE)
    @JsonInclude
    @ToString.Exclude
    private Data data;
}
