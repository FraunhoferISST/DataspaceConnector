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

import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.controller.message.ids.helper.base.IdsHelperProcessor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Finds the resource required for sending a ResourceUpdate- or ResourceUnavailableMessage and sets
 * it as the exchange's body.
 */
@Component("ResourceFinder")
@RequiredArgsConstructor
public
class ResourceFinder extends IdsHelperProcessor {

    /**
     * Service for ids resources.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Finds the resource with the ID from the exchange properties and sets it as the exchange's
     * body.
     *
     * @param exchange the exchange.
     * @throws ResourceNotFoundException if no resource with the given ID exists.
     */
    @Override
    protected void processInternal(final Exchange exchange) throws ResourceNotFoundException {
        final var resourceId = exchange.getProperty(ParameterUtils.RESOURCE_ID_PARAM, URI.class);
        final var resource = connectorService.getOfferedResourceById(resourceId);

        if (resource.isEmpty()) {
            throw new ResourceNotFoundException("Could not find resource with ID " + resourceId);
        }

        exchange.getIn().setBody(resource.get());
    }
}
