package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import org.springframework.stereotype.Component;

@Component
public class RuleViewFactory implements BaseViewFactory<ContractRule, RuleView> {
    @Override
    public RuleView create(final ContractRule contractRule) {
        final var view = new RuleView();
        view.setTitle(contractRule.getTitle());
        view.setValue(contractRule.getValue());

        return view;
    }
}
