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
package io.dataspaceconnector.common.routing;

import io.dataspaceconnector.common.routing.dataretrieval.DataRetrievalService;
import io.dataspaceconnector.common.routing.dataretrieval.Response;
import io.dataspaceconnector.common.exception.DataRetrievalException;
import io.dataspaceconnector.common.exception.NotImplemented;
import io.dataspaceconnector.common.net.HttpAuthentication;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.util.UUIDUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Implementation of the DataRetrievalService that retrieves data using Camel routes.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class RouteDataRetriever implements DataRetrievalService {

    /**
     * Template for triggering Camel routes.
     */
    private final @NonNull ProducerTemplate template;

    /**
     * The CamelContext required for constructing the {@link ProducerTemplate}.
     */
    private final @NonNull CamelContext context;

    /**
     * Retrieves and returns the data using a Camel route. The route to use is identified by the
     * access URL, which should be the ID of a route.
     *
     * @param accessUrl The route ID.
     * @param input The query input.
     * @return The data returned by the route wrapped in a RouteResponse.
     */
    public Response get(final URL accessUrl, final QueryInput input) throws DataRetrievalException {
        try {
            final var routeId = UUIDUtils.uuidFromUri(accessUrl.toURI());
            final var camelDirect = "direct:" + routeId;

            final var result = template.send(camelDirect,
                    ExchangeBuilder.anExchange(context).build());

            if (result.getException() != null) {
                throw result.getException();
            } else if (result.getProperty(Exchange.EXCEPTION_CAUGHT) != null) {
                throw result.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
            }

            final var data = result.getIn().getBody(String.class);
            return new RouteResponse(new ByteArrayInputStream(
                    data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to retrieve data. [exception=({})]", e.getMessage(), e);
            }
            throw new DataRetrievalException("Failed to retrieve data." + e.getMessage());
        }

    }

    /**
     * Retrieves the data using authentication. Will throw a {@link NotImplemented}, as all
     * required authentication information is already present in the route.
     */
    @Override
    public Response get(final URL target, final QueryInput input,
                        final List<? extends HttpAuthentication> auth)
            throws DataRetrievalException {
        throw new NotImplemented();
    }

}
