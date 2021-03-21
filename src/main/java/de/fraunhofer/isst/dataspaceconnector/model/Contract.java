package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

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
