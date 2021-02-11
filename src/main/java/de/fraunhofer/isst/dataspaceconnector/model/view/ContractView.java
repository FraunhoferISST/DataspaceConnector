package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import lombok.Data;

import java.util.Set;

@Data
public class ContractView implements BaseView<Contract> {
    private String title;
    private Set<EndpointId> rules;
}
