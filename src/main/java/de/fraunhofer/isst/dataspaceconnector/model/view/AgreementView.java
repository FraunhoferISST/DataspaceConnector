package de.fraunhofer.isst.dataspaceconnector.model.view;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Date;

@Getter
@Setter
@Relation(collectionRelation = "agreement", itemRelation = "agreement")
public class AgreementView extends RepresentationModel<AgreementView> {
    private Date creationDate;
    private Date modificationDate;
    private String value;
}
