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
package io.dataspaceconnector.model.keystore;

import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.util.FactoryUtils;
import java.net.URI;

/**
 * Factory class for the key store.
 */
public class KeystoreFactory extends AbstractFactory<Keystore, KeystoreDesc> {

    /**
     * The default password.
     */
    public static final String DEFAULT_PASSWORD = "";

    /**
     * The default location.
     */
    public static final URI DEFAULT_LOCATION = URI.create("file:///conf/keystore-localhost.p12");

    /**
     * The default alias.
     */
    public static final String DEFAULT_ALIAS = "1";

    @Override
    protected final Keystore initializeEntity(final KeystoreDesc desc) {
        return new Keystore();
    }

    @Override
    public final boolean updateInternal(final Keystore keystore, final KeystoreDesc desc) {
        final var hasUpdatedLocation = updateLocation(keystore, desc.getLocation());
        final var hasUpdatedPassword = updatePassword(keystore, desc.getPassword());
        final var hasUpdatedAlias = updateAlias(keystore, desc.getAlias());

        return hasUpdatedLocation || hasUpdatedPassword || hasUpdatedAlias;
    }

    private boolean updatePassword(final Keystore keystore, final String password) {
        if (keystore.getPassword() != null && password == null) {
            return false;
        }

        final var newPassword = FactoryUtils.updateString(keystore.getPassword(),
                password, DEFAULT_PASSWORD);
        newPassword.ifPresent(keystore::setPassword);

        return newPassword.isPresent();
    }

    private boolean updateLocation(final Keystore keystore, final URI location) {
        final var newLocation =
                FactoryUtils.updateUri(keystore.getLocation(), location, DEFAULT_LOCATION);
        newLocation.ifPresent(keystore::setLocation);

        return newLocation.isPresent();
    }

    private boolean updateAlias(final Keystore keystore, final String alias) {
        if (keystore.getAlias() != null && alias == null) {
            return false;
        }

        final var newAlias = FactoryUtils.updateString(keystore.getAlias(),
                alias, DEFAULT_ALIAS);
        newAlias.ifPresent(keystore::setAlias);

        return newAlias.isPresent();
    }
}
