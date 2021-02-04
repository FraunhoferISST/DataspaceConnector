package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractRule;
import org.springframework.stereotype.Component;

@Component
public class RuleViewer implements BaseViewer<ContractRule, RuleView> {
    @Override
    public RuleView create(final ContractRule contractRule) {
        final var view = new RuleView();
        view.setTitle(contractRule.getTitle());
        view.setValue(contractRule.getValue());

        return view;
    }
}
