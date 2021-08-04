package io.dataspaceconnector.service.message.handler.validator;

import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.dto.payload.ContractTargetRuleMapContainer;
import io.dataspaceconnector.service.message.handler.exception.MissingTargetInRuleException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Validates the target rule map for a contract request, that links target artifacts to a list of
 * rules.
 */
@Component("TargetRuleMapValidator")
class TargetRuleMapValidator extends IdsValidator<Request<ContractRequestMessageImpl,
        ContractTargetRuleMapContainer, Optional<Jws<Claims>>>> {

    /**
     * Validates the target rule map for a contract request, that links target artifacts to a
     * list of rules.
     *
     * @param msg the incoming message.
     * @throws Exception if the target is missing for any rules.
     */
    @Override
    protected void processInternal(final Request<ContractRequestMessageImpl,
            ContractTargetRuleMapContainer, Optional<Jws<Claims>>> msg) throws Exception {
        if (msg.getBody().getTargetRuleMap().containsKey(null)) {
            throw new MissingTargetInRuleException(msg.getBody().getContractRequest(),
                    "Rule is missing a target.");
        }
    }

}
