package de.fraunhofer.isst.dataspaceconnector.model.view;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@Relation(collectionRelation = "contract", itemRelation = "contract")
public class ContractView  extends RepresentationModel<ContractView> {
    private Date creationDate;
    private Date modificationDate;
    private String title;
    private Date start;
    private Date end;
    private Map<String, String> additional;
}
