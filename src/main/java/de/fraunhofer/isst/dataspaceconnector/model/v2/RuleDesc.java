package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.Data;

@Data
public class RuleDesc extends BaseDescription<Rule> {
    private String title;
    private String rule;
}
