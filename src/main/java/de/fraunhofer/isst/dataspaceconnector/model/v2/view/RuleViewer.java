package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Rule;
import org.springframework.stereotype.Component;

@Component
public class RuleViewer implements BaseViewer<Rule, RuleView> {
    @Override
    public RuleView create(final Rule rule) {
        final var view = new RuleView();
        view.setTitle(rule.getTitle());
        view.setValue(rule.getValue());

        return view;
    }
}
