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
package io.dataspaceconnector.model.contract;

import io.dataspaceconnector.model.named.AbstractNamedFactory;
import io.dataspaceconnector.model.util.FactoryUtils;
import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * Creates and updates a contract.
 */
public class ContractFactory extends AbstractNamedFactory<Contract, ContractDesc> {

    /**
     * Default remote id assigned to all contracts.
     */
    public static final URI DEFAULT_REMOTE_ID = URI.create("genesis");

    /**
     * Default consumer assigned to all contracts.
     */
    public static final URI DEFAULT_CONSUMER = URI.create("");

    /**
     * Default provider assigned to all contracts.
     */
    public static final URI DEFAULT_PROVIDER = URI.create("");

    /**
     * Create a new contract.
     *
     * @param desc The description of the new contract.
     * @return The new contract.
     * @throws IllegalArgumentException if desc is null.
     */
    @Override
    protected Contract initializeEntity(final ContractDesc desc) {
        final var contract = new Contract();
        contract.setRules(new ArrayList<>());
        contract.setResources(new ArrayList<>());

        return contract;
    }

    /**
     * Update a contract.
     *
     * @param contract The contract to be updated.
     * @param desc     The new contract description.
     * @return True if the contract has been modified.
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Override
    protected boolean updateInternal(final Contract contract, final ContractDesc desc) {
        final var hasUpdatedRemoteId = this.updateRemoteId(contract, desc.getRemoteId());
        final var hasUpdatedConsumer = this.updateConsumer(contract, desc.getConsumer());
        final var hasUpdatedProvider = this.updateProvider(contract, desc.getProvider());

        final var hasUpdatedTime = this.updateTime(contract, desc.getStart(), desc.getEnd());

        return hasUpdatedRemoteId || hasUpdatedConsumer || hasUpdatedProvider || hasUpdatedTime;
    }

    private boolean updateRemoteId(final Contract contract, final URI remoteId) {
        final var newUri =
                FactoryUtils.updateUri(contract.getRemoteId(), remoteId, DEFAULT_REMOTE_ID);
        newUri.ifPresent(contract::setRemoteId);

        return newUri.isPresent();
    }

    private boolean updateConsumer(final Contract contract, final URI consumer) {
        final var newUri =
                FactoryUtils.updateUri(contract.getConsumer(), consumer, DEFAULT_CONSUMER);
        newUri.ifPresent(contract::setConsumer);

        return newUri.isPresent();
    }

    private boolean updateProvider(final Contract contract, final URI provider) {
        final var newUri =
                FactoryUtils.updateUri(contract.getProvider(), provider, DEFAULT_PROVIDER);
        newUri.ifPresent(contract::setProvider);

        return newUri.isPresent();
    }

    private boolean updateTime(final Contract contract, final ZonedDateTime start,
                               final ZonedDateTime end) {
        final var defaultTime = ZonedDateTime.now(ZoneOffset.UTC);
        final var newStart = FactoryUtils.updateDate(contract.getStart(), start, defaultTime);
        final var newEnd = FactoryUtils.updateDate(contract.getEnd(), end, defaultTime);

        // Validate the state of the contract with the new times
        var realStart = newStart.orElseGet(contract::getStart);
        final var realEnd = newEnd.orElseGet(contract::getEnd);

        if (realStart.isAfter(realEnd)) {
            // Invalid state, fix up
            realStart = realEnd;
        }

        // Reiterate the operation
        final var finalStartValue =
                FactoryUtils.updateDate(contract.getStart(), realStart, defaultTime);
        final var finalEndValue = FactoryUtils.updateDate(contract.getEnd(), realEnd, defaultTime);

        finalStartValue.ifPresent(contract::setStart);
        finalEndValue.ifPresent(contract::setEnd);

        return finalStartValue.isPresent() || finalEndValue.isPresent();
    }
}
