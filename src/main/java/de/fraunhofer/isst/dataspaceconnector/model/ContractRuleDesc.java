package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

/**
 * The description of a contract rule.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContractRuleDesc extends AbstractDescription<ContractRule> {

    /**
     * The rule id on provider side.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private URI remoteId;

    /**
     * The title of the rule.
     */
    private String title;

    /**
     * The rule to be enforced.
     */
    private String value;
}
