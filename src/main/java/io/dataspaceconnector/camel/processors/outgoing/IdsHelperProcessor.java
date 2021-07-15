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
package io.dataspaceconnector.camel.processors.outgoing;

import java.net.URI;

import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import io.dataspaceconnector.camel.exception.InvalidResponseException;
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

public abstract class IdsHelperProcessor implements Processor {

    @Override
    public void process(final Exchange exchange) throws Exception {
        processInternal(exchange);
    }

    protected abstract void processInternal(Exchange exchange) throws Exception;

}

@Component("ResourceFinder")
@RequiredArgsConstructor
class ResourceFinder extends IdsHelperProcessor {

    /**
     * Service for ids resources.
     */
    private final @NonNull ConnectorService connectorService;

    @Override
    protected void processInternal(final Exchange exchange) {
        final var resourceId = exchange.getProperty("resourceId", URI.class);
        final var resource = connectorService.getOfferedResourceById(resourceId);

        if (resource.isEmpty()) {
            throw new ResourceNotFoundException("Could not find resource with ID " + resourceId);
        }

        exchange.getIn().setBody(resource.get());
    }
}

@Component("ConfigurationUpdater")
@RequiredArgsConstructor
class ConfigurationUpdater extends IdsHelperProcessor {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    @Override
    protected void processInternal(final Exchange exchange) throws ConfigUpdateException {
        // Update the config model.
        connectorService.updateConfigModel();
    }

}

@Component("PolicyRestrictionProcessor")
@Log4j2
class PolicyRestrictionProcessor extends IdsHelperProcessor {

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
