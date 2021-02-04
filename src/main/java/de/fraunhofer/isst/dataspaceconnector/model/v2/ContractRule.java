package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A ContractRule defines a rule that should be enforced.
 */
@Data
@Entity
@Table
@EqualsAndHashCode(callSuper = false)
@Setter(AccessLevel.PACKAGE)
public class ContractRule extends BaseResource {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The title of the rule.
     */
    private String title;

    /**
     * The definition of the rule.
     **/
    private String value;
}
