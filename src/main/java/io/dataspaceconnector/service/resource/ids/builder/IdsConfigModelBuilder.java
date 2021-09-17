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
package io.dataspaceconnector.service.resource.ids.builder;

import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.iais.eis.ConfigurationModelBuilder;
import de.fraunhofer.iais.eis.ConnectorStatus;
import de.fraunhofer.iais.eis.LogLevel;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.common.ids.mapping.ToIdsObjectMapper;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.model.configuration.Configuration;
import io.dataspaceconnector.service.resource.ids.builder.base.AbstractIdsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Converts dsc configuration to ids configuration.
 */
@Component
public final class IdsConfigModelBuilder extends AbstractIdsBuilder<Configuration,
        ConfigurationModel> {

    /**
     * Constructs an IdsConfigModelBuilder.
     *
     * @param selfLinkHelper the self link helper.
     */
    @Autowired
    public IdsConfigModelBuilder(final SelfLinkHelper selfLinkHelper) {
        super(selfLinkHelper);
    }

    @Override
    protected ConfigurationModel createInternal(final Configuration config,
                                                final int currentDepth,
                                                final int maxDepth)
            throws ConstraintViolationException {
        // Prepare configuration attributes.
        final var deployMode = ToIdsObjectMapper.getConnectorDeployMode(config.getDeployMode());
        final var logLevel = config.getLogLevel() == null
                ? LogLevel.MINIMAL_LOGGING
                : ToIdsObjectMapper.getLogLevel(config.getLogLevel());
        final var connector = ToIdsObjectMapper.getConnectorFromConfiguration(config);
        final var status = config.getStatus() == null
                ? ConnectorStatus.CONNECTOR_OFFLINE
                : ToIdsObjectMapper.getConnectorStatus(config.getStatus());

        final var configBuilder = new ConfigurationModelBuilder()
                ._connectorDeployMode_(deployMode)
                ._keyStore_(config.getKeystore().getLocation())
                ._keyStorePassword_(config.getKeystore().getPassword())
                ._keyStoreAlias_(config.getKeystore().getAlias())
                ._trustStore_(config.getTruststore().getLocation())
                ._trustStorePassword_(config.getTruststore().getPassword())
                ._trustStoreAlias_(config.getTruststore().getAlias())
                ._configurationModelLogLevel_(logLevel)
                ._connectorStatus_(status)
                ._connectorDescription_(connector);

        if (config.getProxy() != null) {
            configBuilder._connectorProxy_(config.getProxy() == null
                    ? null : List.of(ToIdsObjectMapper.getProxy(config.getProxy())));
        }

        return configBuilder.build();
    }
}
