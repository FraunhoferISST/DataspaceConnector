package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import lombok.Data;

import java.util.Set;

@Data
public class ContractView implements BaseView<Contract> {
    private String title;
    private Set<EndpointId> rules;
}
