package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractRule;
import lombok.Data;

@Data
public class RuleView implements BaseView<ContractRule> {
    private String title;
    private String value;
}
