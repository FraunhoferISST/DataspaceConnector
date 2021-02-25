package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.Set;

@Data
public class ContractView  extends RepresentationModel<ContractView> {
    private String title;
    private Set<EndpointId> rules;
}
