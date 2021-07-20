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

import java.net.URI;

import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.util.MetadataUtils;
import org.springframework.stereotype.Component;

/**
 * Factory class for the trust store.
 */
@Component
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

        return hasUpdatedLocation || hasUpdatedPassword;
    }

    private boolean updatePassword(final Truststore truststore, final String password) {
        if (truststore.getPassword() != null && password == null) {
            return false;
        }

        final var newPassword = MetadataUtils.updateString(truststore.getPassword(),
                password, DEFAULT_PASSWORD);
        newPassword.ifPresent(truststore::setPassword);

        return newPassword.isPresent();
    }

    private boolean updateLocation(final Truststore truststore, final URI location) {
        final var newLocation =
                MetadataUtils.updateUri(truststore.getLocation(), location, DEFAULT_LOCATION);
        newLocation.ifPresent(truststore::setLocation);

        return newLocation.isPresent();
    }
}
