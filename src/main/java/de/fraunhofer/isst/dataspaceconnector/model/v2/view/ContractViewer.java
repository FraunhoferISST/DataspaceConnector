package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class ContractViewer implements BaseViewer<Contract, ContractView> {
    @Autowired
    private EndpointService endpointService;

    @Override
    public ContractView create(final Contract contract) {
        final var view = new ContractView();
        view.setTitle(contract.getTitle());

        final var allRuleIds = contract.getRules().keySet();
        final var allRuleEndpoints = new HashSet<EndpointId>();

        for(final var ruleId : allRuleIds) {
            allRuleEndpoints.addAll(endpointService.getByEntity(ruleId));
        }

        view.setRules(allRuleEndpoints);

        return view;
    }
}
