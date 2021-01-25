package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RuleDesc extends BaseDescription<Rule> {
    private String title;
    private String rule;
}
