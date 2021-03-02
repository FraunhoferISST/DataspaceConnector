package de.fraunhofer.isst.dataspaceconnector.model.view;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class RuleView extends RepresentationModel<RuleView> {
    private String title;
    private String value;
}
