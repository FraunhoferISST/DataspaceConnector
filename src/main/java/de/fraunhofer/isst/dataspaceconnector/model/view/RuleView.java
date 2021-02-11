package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import lombok.Data;

@Data
public class RuleView implements BaseView<ContractRule> {
    private String title;
    private String value;
}
