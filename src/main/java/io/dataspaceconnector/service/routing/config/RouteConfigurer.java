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
package io.dataspaceconnector.service.routing.config;

import de.fraunhofer.iais.eis.AppEndpoint;
import de.fraunhofer.iais.eis.AppRoute;
import de.fraunhofer.iais.eis.ConnectorEndpoint;
import de.fraunhofer.iais.eis.GenericEndpoint;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Utility class for configuring Camel routes for the Dataspace Connector.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class RouteConfigurer {
    /**
     * Username for the Dataspace Connector.
     */
    @Setter
    @Value("${spring.security.user.name}")
    private String dataSpaceConnectorApiUsername;

    /**
     * Password for the Dataspace Connector.
     */
    @Setter
    @Value("${spring.security.user.password}")
    private String dataSpaceConnectorApiPassword;

    /**
     * The Freemarker configuration.
     */
    private final @NonNull Configuration freemarkerConfig;

    /**
     * Adds basic authentication information for the Dataspace Connector to the input map
     * for creating a Camel XML route to be used with the Dataspace Connector.
     *
     * @param freemarkerInput the map containing the values to insert into the route template
     */
    public void addBasicAuthToContext(final Map<String, Object> freemarkerInput) {
        final var auth = dataSpaceConnectorApiUsername + ":" + dataSpaceConnectorApiPassword;
        final var encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
        final var authHeader = "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);
        freemarkerInput.put("connectorAuthHeader", authHeader);
    }

    /**
     * Chooses and returns the route template for the Dataspace Connector based on the app route.
     *
     * @param appRoute the app route
     * @return the route template
     */
    public Template getRouteTemplate(final AppRoute appRoute) {
        final var routeStart = appRoute.getAppRouteStart();
        final var routeEnd = appRoute.getAppRouteEnd();

        // Possible combinations:
        // Connector -> Generic
        // Generic -> Connector
        // Connector -> App
        // App -> Connector
        // Generic -> App
        // App -> Generic
        // App -> App

        Template template = null;
        try {
            if (routeStart == null || routeStart.isEmpty()
                    || routeStart.get(0) instanceof ConnectorEndpoint
                    || routeStart.get(0) == null) {
                if (routeEnd.get(0) instanceof GenericEndpoint
                        || routeEnd.get(0) instanceof AppEndpoint) {
                    template = freemarkerConfig.getTemplate("connector_to_generic_template.ftl");
                }
            } else if ((routeStart.get(0) instanceof GenericEndpoint
                    || routeStart.get(0) instanceof AppEndpoint)
                    && routeEnd.get(0) instanceof ConnectorEndpoint) {
                template = freemarkerConfig.getTemplate("generic_to_connector_template.ftl");
            } else if (routeStart.get(0) instanceof GenericEndpoint
                    && routeEnd.get(0) instanceof AppEndpoint) {
                template = freemarkerConfig.getTemplate("generic_to_app_template.ftl");
            } else if (routeStart.get(0) instanceof AppEndpoint
                    && routeEnd.get(0) instanceof GenericEndpoint) {
                template = freemarkerConfig.getTemplate("app_to_generic_template.ftl");
            } else if (routeStart.get(0) instanceof AppEndpoint
                    && routeEnd.get(0) instanceof AppEndpoint) {
                template = freemarkerConfig.getTemplate("app_to_app_template.ftl");
            } else {
                template = null;
            }
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to get route template. [exception=({})].", e.getMessage());
            }
            template = null;
        }

        return template;
    }
}
