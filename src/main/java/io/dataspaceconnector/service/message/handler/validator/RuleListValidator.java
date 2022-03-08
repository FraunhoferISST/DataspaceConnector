/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.service.message.handler.validator;

import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.dto.payload.ContractRuleListContainer;
import io.dataspaceconnector.service.message.handler.exception.MissingRulesException;
import io.dataspaceconnector.service.message.handler.validator.base.IdsValidator;
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
