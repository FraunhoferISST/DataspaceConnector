/*
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
package io.configmanager.extensions.routes.camel.connector.trustedconnector;

import de.fraunhofer.iais.eis.AppRoute;
import de.fraunhofer.iais.eis.ConfigurationModel;
import de.fraunhofer.iais.eis.ConnectorEndpoint;
import de.fraunhofer.iais.eis.GenericEndpoint;
import lombok.experimental.UtilityClass;
import org.apache.velocity.VelocityContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.net.URI;

/**
 * Utility class for configuring Camel routes for the TrustedConnector.
 */
@UtilityClass
public final class TrustedConnectorRouteConfigurer {

    /**
     * ResourceLoader for loading Camel route templates from the classpath.
     */
    private static final ResourceLoader RESOURCE_LOADER = new DefaultResourceLoader();

    /**
     * Adds key- and truststore information to the Velocity context for creating a Camel XML route to be used with
     * the Trusted Connector.
     *
     * @param velocityContext the context containing the values to insert into the route template
     * @param configurationModel the config model containing key- and truststore information
     */
    public static void addSslConfig(final VelocityContext velocityContext, final ConfigurationModel configurationModel) {
        velocityContext.put("keyStorePath", removeFileScheme(configurationModel.getKeyStore()));
        velocityContext.put("keyStorePassword", configurationModel.getKeyStorePassword());
        velocityContext.put("trustStorePath", removeFileScheme(configurationModel.getTrustStore()));
        velocityContext.put("trustStorePassword", configurationModel.getTrustStorePassword());
    }

    /**
     * Chooses and returns the route template for the Trusted Connector based on the app route.
     *
     * @param appRoute the app route
     * @return the route template
     */
    public static Resource getRouteTemplate(final AppRoute appRoute) {
        final var routeStart = appRoute.getAppRouteStart();

        Resource resource;
        if (routeStart.get(0) instanceof GenericEndpoint) {
            resource = RESOURCE_LOADER.getResource("classpath:camel-templates/trustedconnector/idscp2_client_template_1.vm");
        } else if (routeStart.get(0) instanceof ConnectorEndpoint) {
            resource = RESOURCE_LOADER.getResource("classpath:camel-templates/trustedconnector/idscp2_server_template_1.vm");
        } else {
            resource = null;
        }

        return resource;
    }

    /**
     * Removes the file scheme from an URI, if it is specified.
     *
     * @param uri the URI
     * @return the URI as a string with the file scheme removed, if it was present.
     */
    private static String removeFileScheme(final URI uri) {
        var string = uri.toString();

        if (string.startsWith("file://")) {
            string = string.substring(7);
        } else if (string.startsWith("file:")) {
            string = string.substring(5);
        }

        return string;
    }
}
