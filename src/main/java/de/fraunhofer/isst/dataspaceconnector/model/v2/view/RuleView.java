package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Rule;
import lombok.Data;

@Data
public class RuleView implements BaseView<Rule> {
    private String title;
    private String value;
}
