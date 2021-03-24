package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * A contract documents access and usage behaviours.
 */
@Data
@Entity
@Table
@EqualsAndHashCode(callSuper = false)
@Setter(AccessLevel.PACKAGE)
public class Contract extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The contract id on provider side.
     */
    private URI remoteId;

    /**
     * The consumer signing the contract.
     */
    private URI consumer;

    /**
     * The provider signing the contract.
     */
    private URI provider;

    /**
     * The date of the contract conclusion.
     */
    private Date date;

    /**
     * The title of the contract.
     */
    private String title;

    /**
     * Contract start time and date.
     */
    private Date start;

    /**
     * Contract end time and date.
     */
    private Date end;

    /**
     * The rules used by this contract.
     **/
    @ManyToMany
    private List<ContractRule> rules;

    /**
     * The representations in which this contract is used.
     */
    @ManyToMany(mappedBy = "contracts")
    private List<Resource> resources;
}
