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

import io.dataspaceconnector.model.Configuration;
import io.dataspaceconnector.model.ConfigurationDesc;
import io.dataspaceconnector.model.ConfigurationFactory;
import io.dataspaceconnector.model.Proxy;
import io.dataspaceconnector.repositories.ProxyRepository;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.UUID;

/**
 * Service class for the configuration.
 */
@Log4j2
@Service
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.NONE)
public class ConfigurationService extends BaseEntityService<Configuration, ConfigurationDesc> {

    /**
     * Repository for storing data.
     **/
    private final @NonNull ProxyRepository proxyRepo;

    /**
     * Constructor for ConfigurationService.
     * @param proxyRepository The proxy repository.
     */
    @Autowired
    public ConfigurationService(final @NonNull ProxyRepository proxyRepository) {
        this.proxyRepo = proxyRepository;
    }

    /**
     * Persist the configuration with the proxy.
     * @param configuration The configuration which is persisted.
     * @return The persisted configuration.
     */
    @Override
    protected Configuration persist(final Configuration configuration) {
        if (configuration.getProxy() != null) {
            proxyRepo.saveAndFlush(configuration.getProxy());
        }

        return super.persist(configuration);
    }

    /**
     *
     * @param configurationId The id of the configuration which is update.
     * @param proxy The new proxy.
     * @throws IOException Exception occurs, if proxy can not be set at configuration.
     */
    @Transactional
    public void setConfigurationProxyInformation(final UUID configurationId,
                                                 final Proxy proxy)
            throws IOException {
        final var configurationRepository = getRepository();
        final var configuration = configurationRepository.findById(configurationId).orElse(null);
        if (configuration != null && proxy != null) {

            final var updatedConfiguration = ((ConfigurationFactory) getFactory())
                    .updateProxy(configuration, proxy);
            persist(updatedConfiguration);
        } else {
            throw new IOException("Failed to update the configuration");
        }
    }
}
