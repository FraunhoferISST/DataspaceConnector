package de.fraunhofer.isst.dataspaceconnector.model.view;

import java.net.URI;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Setter
@Relation(collectionRelation = "agreements", itemRelation = "agreement")
public class AgreementView extends RepresentationModel<AgreementView> {
    private Date   creationDate;
    private Date   modificationDate;
    private URI    remoteId;
    private boolean confirmed;
    private String value;
}
