package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The description of a contract rule.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContractRuleDesc extends AbstractDescription<ContractRule> {
    /**
     * The title of the rule.
     */
    private String title;

    /**
     * The rule to be enforced.
     */
    private String content;
}
