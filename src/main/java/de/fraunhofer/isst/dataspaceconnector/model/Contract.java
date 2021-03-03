package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
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
     * The rules used by this contract.
     **/
    @ManyToMany
    private List<ContractRule> rules;
}
