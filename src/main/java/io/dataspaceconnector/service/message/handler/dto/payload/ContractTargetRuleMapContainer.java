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
package io.dataspaceconnector.service.message.handler.dto.payload;

import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.Rule;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * DTO used in the contract request handler route to transfer the contract request as well as its
 * target-rule-map tat links rules to target artifacts, so that the map only has to be created once.
 */
@Getter
@AllArgsConstructor
public class ContractTargetRuleMapContainer {

    /**
     * The contract request.
     */
    private final ContractRequest contractRequest;

    /**
     * The target-rule-map of the contract request that links rules to target artifacts.
     */
    private final Map<URI, List<Rule>> targetRuleMap;

}
