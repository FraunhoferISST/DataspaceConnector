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
package io.dataspaceconnector.service.resource.relation;

import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.resource.Resource;
import io.dataspaceconnector.service.resource.base.OwningRelationService;
import io.dataspaceconnector.service.resource.type.ContractService;
import io.dataspaceconnector.service.resource.type.ResourceService;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Base class for handling resource-contract relations.
 *
 * @param <T> The resource type.
 */
@NoArgsConstructor
public abstract class AbstractResourceContractLinker<T extends Resource>
        extends OwningRelationService<T, Contract, ResourceService<T, ?>, ContractService> {
    /**
     * Get the list of contracts owned by the resource.
     *
     * @param owner The owner of the contracts.
     * @return The list of owned contracts.
     */
    @Override
    protected List<Contract> getInternal(final Resource owner) {
        return owner.getContracts();
    }
}
