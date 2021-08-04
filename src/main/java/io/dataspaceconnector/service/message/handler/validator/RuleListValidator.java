package io.dataspaceconnector.service.message.handler.validator;

import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.dto.payload.ContractRuleListContainer;
import io.dataspaceconnector.service.message.handler.exception.MissingRulesException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Validates the rules from a contract request.
 */
@Component("RuleListValidator")
class RuleListValidator extends IdsValidator<Request<ContractRequestMessageImpl,
        ContractRuleListContainer, Optional<Jws<Claims>>>> {

    /**
     * Checks whether the list of rules from a contract request is empty.
     *
     * @param msg the incoming message.
     * @throws Exception if the list of rules is empty.
     */
    @Override
    protected void processInternal(final Request<ContractRequestMessageImpl,
            ContractRuleListContainer, Optional<Jws<Claims>>> msg) throws Exception {
        if (msg.getBody().getRules().isEmpty()) {
            throw new MissingRulesException(msg.getBody().getContractRequest(),
                    "Rule list is empty.");
        }
    }

}
