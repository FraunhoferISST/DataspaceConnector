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
package io.dataspaceconnector.config.security;

import javax.annotation.PostConstruct;

import de.fraunhofer.iais.eis.ConnectorDeployMode;
import de.fraunhofer.ids.messaging.core.config.ConfigProducer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

/**
 * Checks the combination of database configuration and connector deployment mode.
 */
@Log4j2
@Configuration
@ConditionalOnExpression("'${spring.datasource.url}'.startsWith('jdbc:h2')")
@RequiredArgsConstructor
public class DatabaseConfigCheck {

    /**
     * Provides the connector configuration.
     */
    private final @NonNull ConfigProducer configProducer;

    /**
     * Issues a log message on WARN level, if an H2 database is used in productive mode.
     */
    @PostConstruct
    public final void checkDatabaseConfig() {
        var mode = configProducer.getConfigContainer()
                .getConfigurationModel().getConnectorDeployMode();
        if (mode == ConnectorDeployMode.PRODUCTIVE_DEPLOYMENT && log.isWarnEnabled()) {
            log.warn("You are using an H2 database in productive mode."
                    + " This is strongly discouraged!");
        }
    }

}
