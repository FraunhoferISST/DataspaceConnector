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

import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import io.dataspaceconnector.common.ids.policy.ContractUtils;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.dataspaceconnector.service.message.handler.dto.payload.ContractRuleListContainer;
import io.dataspaceconnector.service.message.handler.dto.payload.ContractTargetRuleMapContainer;
import io.dataspaceconnector.service.message.handler.transformer.base.IdsTransformer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Transforms the payload of a contract request from a container containing a ContractRequest and
 * its list of rules to a container containing the ContractRequest and its target-rule-map, that
 * links rules to their target artifact.
 */
@Component("ContractTargetRuleMapTransformer")
class ContractTargetRuleMapTransformer extends IdsTransformer<
        Request<ContractRequestMessageImpl, ContractRuleListContainer, Optional<Jws<Claims>>>,
        RouteMsg<ContractRequestMessageImpl, ContractTargetRuleMapContainer>> {

    /**
     * Transforms the payload of the incoming RouteMsg from a container object for a ContractRequest
     * and the list of rules it contains to a container object for the ContractRequest and its
     * rules in relation to their respective targets.
     *
     * @param msg the incoming message.
     * @return a RouteMsg object with the initial header and the new container object as payload.
     * @throws Exception if one of the rules is null.
     */
    @Override
    protected RouteMsg<ContractRequestMessageImpl, ContractTargetRuleMapContainer> processInternal(
            final Request<ContractRequestMessageImpl, ContractRuleListContainer,
                    Optional<Jws<Claims>>> msg) throws Exception {
        final var targetRuleMap = ContractUtils.getTargetRuleMap(msg.getBody().getRules());
        final var container = new ContractTargetRuleMapContainer(msg.getBody().getContractRequest(),
                targetRuleMap);
        return new Request<>(msg.getHeader(), container, msg.getClaims());
    }

}
