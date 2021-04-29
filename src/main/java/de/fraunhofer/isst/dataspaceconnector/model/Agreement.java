package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.net.URI;
import java.util.List;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * A contract agreement is an agreement between two parties on access
 * and usage behaviours.
 */
@Entity
@Table
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Agreement extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The agreement id on provider side.
     */
    private URI remoteId;

    /**
     * Indicates whether both parties have agreed.
     */
    private boolean confirmed;

    /**
     * Signal that this agreement expired and may not be used anymore.
     */
    private boolean archived;

    /**
     * The definition of the contract.
     **/
    @Lob
    private String value;

    /**
     * The artifacts this agreement refers to.
     */
    @ManyToMany
    private List<Artifact> artifacts;
}
