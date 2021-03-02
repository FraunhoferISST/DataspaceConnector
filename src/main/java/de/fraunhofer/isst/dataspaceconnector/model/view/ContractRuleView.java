package de.fraunhofer.isst.dataspaceconnector.model.view;

import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Setter
@Relation(collectionRelation = "rules", itemRelation = "rule")
public class ContractRuleView extends RepresentationModel<ContractRuleView> {
    private Date creationDate;
    private Date modificationDate;
    private String title;
    private String value;
    private Map<String, String> additional;
}
