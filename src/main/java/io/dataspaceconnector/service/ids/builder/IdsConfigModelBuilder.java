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
package io.dataspaceconnector.service.ids.builder;

import java.util.ArrayList;
import java.util.List;

import de.fraunhofer.iais.eis.ConfigurationModelBuilder;
import de.fraunhofer.iais.eis.ConnectorStatus;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.model.configuration.Configuration;
import io.dataspaceconnector.util.IdsUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Converts dsc configuration to ids configuration.
 */
@Component
@RequiredArgsConstructor
public final class IdsConfigModelBuilder extends AbstractIdsBuilder<Configuration,
        de.fraunhofer.iais.eis.ConfigurationModel> {

    @Override
    protected de.fraunhofer.iais.eis.ConfigurationModel createInternal(final Configuration config,
                                                                       final int currentDepth,
                                                                       final int maxDepth)
            throws ConstraintViolationException {
        // Prepare configuration attributes.
        // TODO unmapped: configuration.getVersion()
        // TODO: keystore/truststore don't have alias fields
        final var deployMode = IdsUtils.getConnectorDeployMode(config.getDeployMode());
        final var logLevel = IdsUtils.getLogLevel(config.getLogLevel());
        final var connector = IdsUtils.getConnectorFromConfiguration(config);

        return new ConfigurationModelBuilder()
                ._connectorProxy_(config.getProxy() == null ? new ArrayList<>()
                                          : List.of(IdsUtils.getProxy(config.getProxy())))
                ._connectorDeployMode_(deployMode)
                ._keyStore_(config.getKeystore().getLocation())
                ._keyStorePassword_(config.getKeystore().getPassword())
                ._keyStoreAlias_("")
                ._trustStore_(config.getTruststore().getLocation())
                ._trustStorePassword_(config.getTruststore().getPassword())
                ._trustStoreAlias_("")
                ._configurationModelLogLevel_(logLevel)
                ._connectorStatus_(config.getStatus() != null ? config.getStatus()
                        : ConnectorStatus.CONNECTOR_ONLINE)
                ._connectorDescription_(connector)
                .build();
    }
}
