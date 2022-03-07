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
package io.dataspaceconnector.controller.message.ids.helper;

import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.controller.message.ids.helper.base.IdsHelperProcessor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Updates the connector configuration in preparation for sending a ConnectorUpdate- or
 * ConnectorUnavailableMessage.
 */
@Component("ConfigurationUpdater")
@RequiredArgsConstructor
public
class ConfigurationUpdater extends IdsHelperProcessor {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Updates the connector configuration.
     *
     * @param exchange the exchange.
     * @throws ConfigUpdateException if updating the configuration fails.
     */
    @Override
    protected void processInternal(final Exchange exchange) throws ConfigUpdateException {
        // Update the config model.
        connectorService.updateConfigModel();
    }

}
