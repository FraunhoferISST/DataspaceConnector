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
package io.dataspaceconnector.model.proxy;

import io.dataspaceconnector.model.auth.AuthenticationDesc;
import io.dataspaceconnector.model.auth.BasicAuth;
import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.util.FactoryUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for the proxy.
 */
public class ProxyFactory extends AbstractFactory<Proxy, ProxyDesc> {

    /**
     * The default location.
     */
    public static final URI DEFAULT_LOCATION = URI.create("");

    @Override
    protected final Proxy initializeEntity(final ProxyDesc desc) {
        return new Proxy();
    }

    @Override
    public final boolean updateInternal(final Proxy proxy, final ProxyDesc desc) {
        final var hasUpdatedLocation = updateLocation(proxy, desc.getLocation());
        final var hasUpdatedExclusions = updateExclusions(proxy, desc.getExclusions());
        final var hasUpdatedAuthentication = updateAuthentication(proxy, desc.getAuthentication());

        return hasUpdatedLocation || hasUpdatedExclusions || hasUpdatedAuthentication;
    }

    private boolean updateExclusions(final Proxy proxy, final List<String> exclusions) {
        final var newExclusionList = FactoryUtils.updateStringList(proxy.getExclusions(),
                exclusions, new ArrayList<>());
        newExclusionList.ifPresent(proxy::setExclusions);

        return newExclusionList.isPresent();
    }

    private boolean updateAuthentication(final Proxy proxy, final AuthenticationDesc auth) {
        if (proxy.getAuthentication() == null && auth == null) {
            return false;
        }

        if (auth == null) {
            proxy.setAuthentication(null);
            return true;
        }

        if (auth.getKey() == null && auth.getValue() == null) {
            return false;
        }

        BasicAuth newAuth;
        if (proxy.getAuthentication() != null) {
            newAuth = new BasicAuth(auth.getKey() == null
                                            ? proxy.getAuthentication().getUsername()
                                            : auth.getKey(),
                                    auth.getValue() == null
                                            ? proxy.getAuthentication().getPassword()
                                            : auth.getValue());
        } else {
            newAuth = new BasicAuth(auth);
        }

        if (newAuth.equals(proxy.getAuthentication())) {
            return false;
        }

        proxy.setAuthentication(newAuth);
        return true;
    }

    private boolean updateLocation(final Proxy proxy, final URI location) {
        final var newLocation = FactoryUtils.updateUri(proxy.getLocation(), location,
                DEFAULT_LOCATION);
        newLocation.ifPresent(proxy::setLocation);

        return newLocation.isPresent();
    }

}
