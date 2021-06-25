package io.dataspaceconnector.view;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = "endpoints", itemRelation = "endpoint")
public class EndpointViewProxy extends RepresentationModel<EndpointViewProxy> {

}
