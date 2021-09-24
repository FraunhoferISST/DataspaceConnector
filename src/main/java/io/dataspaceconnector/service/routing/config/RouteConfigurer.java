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
package io.dataspaceconnector.service.routing.config;

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
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
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
     * ResourceLoader for loading Camel route templates from the classpath.
     */
    private static final ResourceLoader RESOURCE_LOADER = new DefaultResourceLoader();

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

        Template template;
        try {
            if (routeStart.get(0) instanceof GenericEndpoint) {
                template = freemarkerConfig.getTemplate("http_to_connector_template.ftl");
            } else if (routeStart.get(0) instanceof ConnectorEndpoint
                    || routeStart.get(0) == null) {
                template = freemarkerConfig.getTemplate("connector_to_http_template.ftl");
            } else {
                template = null;
            }
        } catch (IOException exception) {
            if (log.isErrorEnabled()) {
                log.error("Failed to get route template. [exception=({})].",
                        exception.getMessage());
            }
            template = null;
        }

        return template;
    }
}
