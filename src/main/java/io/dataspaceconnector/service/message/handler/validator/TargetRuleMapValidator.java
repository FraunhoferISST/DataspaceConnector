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
import io.dataspaceconnector.service.message.handler.dto.payload.ContractTargetRuleMapContainer;
import io.dataspaceconnector.service.message.handler.exception.MissingTargetInRuleException;
import io.dataspaceconnector.service.message.handler.validator.base.IdsValidator;
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
