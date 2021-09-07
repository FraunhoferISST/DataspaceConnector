/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.model.truststore;

import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.util.FactoryUtils;
import java.net.URI;

/**
 * Factory class for the trust store.
 */
public class TruststoreFactory extends AbstractFactory<Truststore, TruststoreDesc> {

    /**
     * The default password.
     */
    public static final String DEFAULT_PASSWORD = "";

    /**
     * The default location.
     */
    public static final URI DEFAULT_LOCATION = URI.create("file:///conf/truststore.p12");

    /**
     * The default alias.
     */
    public static final String DEFAULT_ALIAS = "1";

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Truststore initializeEntity(final TruststoreDesc desc) {
        return new Truststore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean updateInternal(final Truststore truststore, final TruststoreDesc desc) {
        final var hasUpdatedLocation = updateLocation(truststore, desc.getLocation());
        final var hasUpdatedPassword = updatePassword(truststore, desc.getPassword());
        final var hasUpdatedAlias = updateAlias(truststore, desc.getAlias());

        return hasUpdatedLocation || hasUpdatedPassword || hasUpdatedAlias;
    }

    private boolean updatePassword(final Truststore truststore, final String password) {
        if (truststore.getPassword() != null && password == null) {
            return false;
        }

        final var newPassword = FactoryUtils.updateString(truststore.getPassword(),
                password, DEFAULT_PASSWORD);
        newPassword.ifPresent(truststore::setPassword);

        return newPassword.isPresent();
    }

    private boolean updateLocation(final Truststore truststore, final URI location) {
        final var newLocation =
                FactoryUtils.updateUri(truststore.getLocation(), location, DEFAULT_LOCATION);
        newLocation.ifPresent(truststore::setLocation);

        return newLocation.isPresent();
    }

    private boolean updateAlias(final Truststore truststore, final String alias) {
        if (truststore.getAlias() != null && alias == null) {
            return false;
        }

        final var newAlias = FactoryUtils.updateString(truststore.getAlias(),
                alias, DEFAULT_ALIAS);
        newAlias.ifPresent(truststore::setAlias);

        return newAlias.isPresent();
    }
}
