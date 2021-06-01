package io.dataspaceconnector.services.messages.handler;

import java.util.List;

import de.fraunhofer.iais.eis.Rule;

public class RuleExistsValidator extends IdsValidator<RouteMsg<?, List<Rule>>>  {

    @Override
    protected void processInternal(RouteMsg<?, List<Rule>> msg) throws Exception {
        if (rules.isEmpty()) {
            // Return rejection message if the contract request is missing rules.
            return responseService.handleMissingRules(request, messageId, issuer);
        }
    }

}
