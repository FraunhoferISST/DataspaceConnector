package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.net.URI;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * A contract agreement is an agreement between two parties on access and usage behaviours.
 */
@Data
@Entity
@Table
@EqualsAndHashCode(callSuper = false)
@Setter(AccessLevel.PACKAGE)
public class Agreement extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * Indicates whether both parties have agreed.
     */
    private boolean confirmed;

    /**
     * The agreement id on provider side.
     */
    private URI remoteId;

    /**
     * The definition of the contract.
     **/
    private String value;

    /**
     * The artifacts this agreement refers to.
     */
    @ManyToMany
    private List<Artifact> artifacts;
}
