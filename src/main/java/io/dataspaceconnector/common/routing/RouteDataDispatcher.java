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
package io.dataspaceconnector.common.routing;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import io.dataspaceconnector.common.exception.DataDispatchException;
import io.dataspaceconnector.common.util.UUIDUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.stereotype.Component;

/**
 * Dispatches data via Camel routes.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class RouteDataDispatcher {

    /**
     * Template for triggering Camel routes.
     */
    private final @NonNull ProducerTemplate template;

    /**
     * The CamelContext required for constructing the {@link ProducerTemplate}.
     */
    private final @NonNull CamelContext context;

    /**
     * Dispatches data via the specified route. The route will be triggered once with the data
     * as the initial input.
     *
     * @param routeId the route ID.
     * @param bytes the data as byte array.
     * @throws IOException if the data cannot be read.
     * @throws DataDispatchException if an error occurs during route execution.
     */
    public void send(final URI routeId, final byte[] bytes) throws IOException,
            DataDispatchException {
        //TODO UTF-8 fails for locally stored data (is UTF-16)
        final var data = new String(bytes, StandardCharsets.UTF_8);
        final var routeUuid = UUIDUtils.uuidFromUri(routeId);
        final var camelDirect = "direct:" + routeUuid;

        try {
            final var result = template
                    .send(camelDirect, ExchangeBuilder.anExchange(context).withBody(data).build());

            if (result.getException() != null) {
                throw result.getException();
            }
        } catch (Exception exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to dispatch data. [exception=({})]", exception.getMessage(),
                        exception);
            }

            throw new DataDispatchException("Failed to dispatch data: " + exception.getMessage());
        }
    }

}
