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
package io.dataspaceconnector.service.message.handler.transformer;

import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import io.dataspaceconnector.common.ids.policy.ContractUtils;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.dataspaceconnector.service.message.handler.dto.payload.ContractRuleListContainer;
import io.dataspaceconnector.service.message.handler.transformer.base.IdsTransformer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Transforms the payload of a contract request from a ContractRequest object to a container object
 * for the ContractRequest and the list of rules it contains.
 */
@Component("ContractRuleListTransformer")
class ContractRuleListTransformer extends IdsTransformer<
        Request<ContractRequestMessageImpl, ContractRequest, Optional<Jws<Claims>>>,
        RouteMsg<ContractRequestMessageImpl, ContractRuleListContainer>> {

    /**
     * Transforms the payload of the incoming RouteMsg from a ContractRequest to a container object
     * for the ContractRequest and the list of rules it contains.
     *
     * @param msg the incoming message.
     * @return a RouteMsg object with the initial header and the container object as payload.
     * @throws Exception if the contract request is null.
     */
    @Override
    protected RouteMsg<ContractRequestMessageImpl, ContractRuleListContainer> processInternal(
            final Request<ContractRequestMessageImpl, ContractRequest, Optional<Jws<Claims>>> msg)
            throws Exception {
        final var request = msg.getBody();
        final var rules = ContractUtils.extractRulesFromContract(request);
        return new Request<>(msg.getHeader(), new ContractRuleListContainer(request, rules),
                msg.getClaims());
    }

}
