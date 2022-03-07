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
package io.dataspaceconnector.model.agreement;

import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.util.FactoryUtils;

import java.net.URI;
import java.util.ArrayList;

/**
 * Creates and updates a contract.
 */
public class AgreementFactory extends AbstractFactory<Agreement, AgreementDesc> {

    /**
     * The default remote id.
     */
    public static final URI DEFAULT_REMOTE_ID = URI.create("genesis");

    /**
     * The default value.
     */
    public static final String DEFAULT_VALUE = "";

    /**
     * Create a new contract.
     *
     * @param desc The description of the new contract.
     * @return The new contract.
     * @throws IllegalArgumentException if the description is null.
     */
    @Override
    protected Agreement initializeEntity(final AgreementDesc desc) {
        final var agreement = new Agreement();
        agreement.setArtifacts(new ArrayList<>());

        return agreement;
    }

    @Override
    protected final boolean updateInternal(final Agreement entity, final AgreementDesc desc) {
        final var hasUpdatedRemoteId = this.updateRemoteId(entity, desc.getRemoteId());
        final var hasUpdatedConfirmed = this.updateHasConfirmed(entity, desc.isConfirmed());
        final var hasUpdatedValue = this.updateValue(entity, desc.getValue());

        return hasUpdatedRemoteId || hasUpdatedConfirmed || hasUpdatedValue;
    }

    private boolean updateRemoteId(final Agreement agreement, final URI remoteId) {
        final var newUri =
                FactoryUtils.updateUri(agreement.getRemoteId(), remoteId, DEFAULT_REMOTE_ID);
        newUri.ifPresent(agreement::setRemoteId);

        return newUri.isPresent();
    }

    private boolean updateHasConfirmed(final Agreement agreement, final boolean confirmed) {
        if (agreement.isConfirmed() != confirmed) {
            agreement.setConfirmed(confirmed);
            return true;
        }

        return false;
    }

    private boolean updateValue(final Agreement agreement, final String value) {
        final var newValue = FactoryUtils.updateString(agreement.getValue(), value, DEFAULT_VALUE);
        newValue.ifPresent(agreement::setValue);

        return newValue.isPresent();
    }
}
