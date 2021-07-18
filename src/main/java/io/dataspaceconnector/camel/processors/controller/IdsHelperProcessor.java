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
package io.dataspaceconnector.camel.processors.controller;

import java.net.URI;

import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import io.dataspaceconnector.camel.exception.InvalidResponseException;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.exception.PolicyRestrictionException;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import io.dataspaceconnector.service.ids.ConnectorService;
import io.dataspaceconnector.util.ErrorMessages;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * Superclass for all processors that perform helper tasks that do not fit in with any of the other
 * processor types.
 */
public abstract class IdsHelperProcessor implements Processor {

    /**
     * Override of the {@link Processor}'s process method. Calls the implementing class's
     * processInternal method with the {@link Exchange}.
     *
     * @param exchange the exchange.
     * @throws Exception if any error occurs.
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        processInternal(exchange);
    }

    /**
     * Performs the helper task. To be implemented by sub classes.
     *
     * @param exchange the exchange.
     * @throws Exception if any error occurs.
     */
    protected abstract void processInternal(Exchange exchange) throws Exception;

}

/**
 * Finds the resource required for sending a ResourceUpdate- or ResourceUnavailableMessage and
 * sets it as the exchange's body.
 */
@Component("ResourceFinder")
@RequiredArgsConstructor
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

/**
 * Updates the connector configuration in preparation for sending a ConnectorUpdate- or
 * ConnectorUnavailableMessage.
 */
@Component("ConfigurationUpdater")
@RequiredArgsConstructor
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

/**
 * Processes an InvalidResponseException occurred during an artifact request to throw a
 * PolicyRestrictionException.
 */
@Component("PolicyRestrictionProcessor")
@Log4j2
class PolicyRestrictionProcessor extends IdsHelperProcessor {

    /**
     * Throws a PolicyRestrictionException, if the exception that occurred in the route is an
     * InvalidResponseException.
     *
     * @param exchange the exchange.
     * @throws Exception a PolicyRestrictionException.
     */
    @Override
    protected void processInternal(final Exchange exchange) throws Exception {
        final var exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        if (exception instanceof InvalidResponseException) {
            final var content = ((InvalidResponseException) exception).getResponse();
            if (log.isDebugEnabled()) {
                log.debug("Data could not be loaded. [content=({})]", content);
            }

            throw new PolicyRestrictionException(ErrorMessages.POLICY_RESTRICTION);
        }
    }

}
