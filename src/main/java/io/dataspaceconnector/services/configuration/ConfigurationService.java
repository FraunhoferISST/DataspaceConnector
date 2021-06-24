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
package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.configurations.Configuration;
import io.dataspaceconnector.model.configurations.ConfigurationDesc;
import io.dataspaceconnector.repositories.KeystoreRepository;
import io.dataspaceconnector.repositories.ProxyRepository;
import io.dataspaceconnector.repositories.TruststoreRepository;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

/**
 * Service class for the configuration.
 */
@Service
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.NONE)
@RequiredArgsConstructor
public class ConfigurationService extends BaseEntityService<Configuration, ConfigurationDesc> {

    /**
     * Repository for storing data.
     **/
    private final @NonNull ProxyRepository proxyRepo;

    private final @NonNull TruststoreRepository trustStoreRepo;

    private final @NonNull KeystoreRepository keystoreRepo;

    /**
     * Persist the configuration with the proxy.
     * @param configuration The configuration which is persisted.
     * @return The persisted configuration.
     */
    @Override
    protected Configuration persist(final Configuration configuration) {
        if(configuration.getProxy() != null)
            proxyRepo.saveAndFlush(configuration.getProxy());

        if(configuration.getTruststore() != null)
            trustStoreRepo.saveAndFlush(configuration.getTruststore());

        if(configuration.getKeystore() != null)
            keystoreRepo.saveAndFlush(configuration.getKeystore());

        return super.persist(configuration);
    }
}
