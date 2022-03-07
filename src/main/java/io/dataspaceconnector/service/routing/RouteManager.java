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
package io.dataspaceconnector.service.routing;

import de.fraunhofer.iais.eis.AppEndpoint;
import de.fraunhofer.iais.eis.AppEndpointType;
import de.fraunhofer.iais.eis.AppRoute;
import de.fraunhofer.iais.eis.ConnectorEndpoint;
import de.fraunhofer.iais.eis.Endpoint;
import de.fraunhofer.iais.eis.GenericEndpoint;
import de.fraunhofer.iais.eis.RouteStep;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.dataspaceconnector.common.exception.RouteCreationException;
import io.dataspaceconnector.common.exception.RouteDeletionException;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.service.routing.config.RouteConfigurer;
import io.dataspaceconnector.service.routing.dto.RouteStepEndpoint;
import io.dataspaceconnector.service.routing.exception.NoSuitableTemplateException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.CamelContext;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.dataspaceconnector.common.util.Utils.escapeForXml;

/**
 * Component for creating Camel routes from AppRoutes.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class RouteManager {

    /**
     * Setting on how to handle routing errors.
     */
    @Value("${camel.application.error-handler}")
    private String camelErrorHandlerRef;

    /**
     * Helper for configuring Camel routes for the Dataspace Connector.
     */
    private final @NonNull RouteConfigurer routeConfigurer;

    /**
     * Unmarshaller for reading route definitions from XML.
     */
    private final @NonNull Unmarshaller unmarshaller;

    /**
     * The Camel context for deploying routes.
     */
    private final @NonNull CamelContext camelContext;

    /**
     * Creates a Camel XML route from a given app route. The generated XML route is then added to
     * the application's Camel context for execution.
     *
     * @param appRoute the app route to create a Camel route for
     * @throws RouteCreationException if the Camel route cannot be created or deployed
     */
    public void createAndDeployXMLRoute(final AppRoute appRoute) throws RouteCreationException {
        final var freemarkerInput = new HashMap<String, Object>();

        // Create id for Camel route.
        final var camelRouteId = getCamelRouteId(appRoute);
        freemarkerInput.put("routeId", camelRouteId);

        // Get route start and end (will either be connector, app or generic endpoint).
        addRouteStartToContext(freemarkerInput,
                (ArrayList<? extends Endpoint>) appRoute.getAppRouteStart());

        addRouteEndToContext(freemarkerInput,
                (ArrayList<? extends Endpoint>) appRoute.getAppRouteEnd());

        // Get route steps (if any).
        addRouteStepsToContext(freemarkerInput,
                (ArrayList<? extends RouteStep>) appRoute.getHasSubRoute());

        try {
            createAndDeployRoute(appRoute, freemarkerInput);
        } catch (Exception exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to create Camel route. [appRouteId=({}), exception=({})]",
                        appRoute.getId(), exception.getMessage());
            }
            throw new RouteCreationException("Error creating Camel route for AppRoute with ID '"
                    + appRoute.getId() + "'", exception);
        }
    }

    /**
     * Extracts the URL of the {@link AppRoute}'s start and adds it to the input map.
     *
     * @param freemarkerInput the input map.
     * @param routeStart      start of the AppRoute.
     */
    private void addRouteStartToContext(final Map<String, Object> freemarkerInput,
                                        final ArrayList<? extends Endpoint> routeStart)
            throws RouteCreationException {
        // If route starts from connector endpoint, route start is not set
        if (routeStart == null || routeStart.isEmpty()
                || routeStart.get(0) instanceof ConnectorEndpoint) {
            return;
        }
        if (routeStart.get(0) instanceof GenericEndpoint) {
            final var genericEndpoint = (GenericEndpoint) routeStart.get(0);
            freemarkerInput.put("startUrl", escapeForXml(genericEndpoint.getPath()));
            addGenericEndpointAuthHeader(freemarkerInput, genericEndpoint);
        } else if (routeStart.get(0) instanceof AppEndpoint) {
            final var appEndpoint = (AppEndpoint) routeStart.get(0);
            freemarkerInput.put("startUrl", escapeForXml(appEndpoint.getPath()));
        } else {
            if (log.isDebugEnabled()) {
                log.debug("The route start cannot be identified. [endpointId=({})]",
                        routeStart.get(0).getId());
            }
            throw new RouteCreationException("The route start cannot be identified.");
        }
    }

    /**
     * Extracts the URL of the {@link AppRoute}'s end and adds it to the input map.
     *
     * @param freemarkerInput the input map.
     * @param routeEnd        end of the AppRoute.
     */
    private void addRouteEndToContext(final Map<String, Object> freemarkerInput,
                                      final ArrayList<? extends Endpoint> routeEnd)
            throws RouteCreationException {
        if (routeEnd.get(0) instanceof GenericEndpoint) {
            final var genericEndpoint = (GenericEndpoint) routeEnd.get(0);
            freemarkerInput.put("endUrl", escapeForXml(genericEndpoint.getPath()));
            addGenericEndpointAuthHeader(freemarkerInput, genericEndpoint);
        } else if (routeEnd.get(0) instanceof AppEndpoint) {
            final var appEndpoint = (AppEndpoint) routeEnd.get(0);
            freemarkerInput.put("endUrl", escapeForXml(appEndpoint.getPath()));
        } else if (!(routeEnd.get(0) instanceof ConnectorEndpoint)) {
            if (log.isDebugEnabled()) {
                log.debug("The route end cannot be identified. [endpointId=({})]",
                        routeEnd.get(0).getId());
            }
            throw new RouteCreationException("The route end cannot be identified.");
        }
    }

    /**
     * Creates and adds the authentication header for calling a generic endpoint to an input map.
     *
     * @param freemarkerInput the input map.
     * @param genericEndpoint the generic endpoint.
     */
    private void addGenericEndpointAuthHeader(final Map<String, Object> freemarkerInput,
                                              final GenericEndpoint genericEndpoint) {
        if (genericEndpoint.getProperties() != null
                && genericEndpoint.getProperties().get("database") != null
                && genericEndpoint.getProperties().get("database").equals("true")) {
            // If the generic endpoint uses a database, authentication is handled through a bean.
            return;
        }

        final var auth = genericEndpoint.getGenericEndpointAuthentication();
        if (auth != null && auth.getAuthUsername() != null
                && !auth.getAuthUsername().isBlank() && auth.getAuthPassword() != null
                && !auth.getAuthPassword().isBlank()) {

            final var type = (String) auth.getProperties().get("type");
            if ("basic".equals(type)) {
                final var username = auth.getAuthUsername();
                final var password = auth.getAuthPassword();
                final var authString = username + ":" + password;
                final var encodedAuth = Base64
                        .encodeBase64(authString.getBytes(StandardCharsets.UTF_8));
                final var authHeader = "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);
                freemarkerInput.put("genericEndpointAuthHeaderKey", "Authorization");
                freemarkerInput.put("genericEndpointAuthHeaderValue", authHeader);
            } else {
                final var headerKey = auth.getAuthUsername();
                final var headerValue = auth.getAuthPassword();
                freemarkerInput.put("genericEndpointAuthHeaderKey", headerKey);
                freemarkerInput.put("genericEndpointAuthHeaderValue", headerValue);
            }
        }
    }

    /**
     * Extracts the start and end URLs of the {@link AppRoute}'s steps and adds them to the
     * input map.
     *
     * @param freemarkerInput the input map.
     * @param routeSteps      steps of the AppRoute.
     */
    private void addRouteStepsToContext(final Map<String, Object> freemarkerInput,
                                        final ArrayList<? extends RouteStep> routeSteps) {
        final var routeStepEndpoints = new ArrayList<RouteStepEndpoint>();

        if (routeSteps != null) {
            Endpoint lastStepEnd = null;

            for (var i = 0; i < routeSteps.size(); i++) {
                final var routeStep = routeSteps.get(i);

                final var stepStart = routeStep.getAppRouteStart().get(0);

                // If end of last step is same as start of current step, only call endpoint once.
                if (i > 0 && !stepStart.equals(lastStepEnd)) {
                    addRouteStepEndpoint(stepStart, routeStepEndpoints);
                }

                if (i < routeSteps.size() - 1) {
                    final var stepEnd = routeStep.getAppRouteEnd().get(0);
                    addRouteStepEndpoint(stepEnd, routeStepEndpoints);

                    lastStepEnd = stepEnd;
                }
            }
        }

        freemarkerInput.put("routeStepEndpoints", routeStepEndpoints);
    }

    /**
     * Adds a {@link RouteStepEndpoint} representation of an {@link Endpoint} to the given list.
     *
     * @param endpoint the endpoint.
     * @param list     the list.
     */
    private void addRouteStepEndpoint(final Endpoint endpoint, final List<RouteStepEndpoint> list) {
        if (AppEndpoint.class.isAssignableFrom(endpoint.getClass())) {
            final var appEndpoint = (AppEndpoint) endpoint;
            if (appEndpoint.getAppEndpointType() == AppEndpointType.OUTPUT_ENDPOINT) {
                list.add(new RouteStepEndpoint(appEndpoint.getPath(),
                        HttpMethod.GET));
            } else {
                list.add(new RouteStepEndpoint(appEndpoint.getPath(),
                        HttpMethod.POST));
            }
        } else {
            list.add(new RouteStepEndpoint(endpoint.getPath(),
                    HttpMethod.POST));
        }
    }

    /**
     * Creates and deploys a Camel route for the Dataspace Connector. First, Dataspace Connector
     * specific configuration is added to the input map, which should already contain
     * general route information. Then, the correct route template for the given AppRoute object
     * is chosen from the templates. Last, the generated XML route is added to the Camel context.
     *
     * @param appRoute        the AppRoute object.
     * @param freemarkerInput the input map.
     * @throws Exception if the route file cannot be created or deployed.
     */
    private void createAndDeployRoute(final AppRoute appRoute,
                                      final Map<String, Object> freemarkerInput)
            throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("Creating route for Dataspace Connector...");
        }

        // Add reference to Camel-Instance's error handler to input map.
        freemarkerInput.put("errorHandlerRef", camelErrorHandlerRef);

        // Add basic auth header for connector endpoint.
        routeConfigurer.addBasicAuthToContext(freemarkerInput);

        // Choose correct XML template based on route.
        final var template = routeConfigurer.getRouteTemplate(appRoute);

        if (template != null) {
            // Populate route template with properties from input map to create route.
            final var writer = populateTemplate(template, freemarkerInput);

            final var inputStream = new ByteArrayInputStream(writer.toString()
                    .getBytes(StandardCharsets.UTF_8));
            final var routes = (RoutesDefinition) unmarshaller.unmarshal(inputStream);
            camelContext.adapt(ModelCamelContext.class).addRouteDefinitions(routes.getRoutes());
            if (log.isDebugEnabled()) {
                log.debug("Added route to Camel context. [routeId=({})]",
                        freemarkerInput.get("routeId"));
            }
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Template is null. Unable to create XML route file for AppRoute. "
                        + "[routeId=({})]", appRoute.getId());
            }

            throw new NoSuitableTemplateException("No suitable Camel route template found for "
                    + "AppRoute with ID '" + appRoute.getId() + "'");
        }
    }

    /**
     * Populates a given Freemarker template using the values from a given input map.
     *
     * @param template the route template.
     * @param freemarkerInput the context containing the values to insert into the template.
     * @return the populated template as a string.
     * @throws IOException if an error occurs while filling out the route template.
     */
    @SuppressFBWarnings("TEMPLATE_INJECTION_FREEMARKER")
    private StringWriter populateTemplate(final Template template,
                                          final Map<String, Object> freemarkerInput)
            throws IOException, TemplateException {
        final var stringWriter = new StringWriter();

        try {
            template.process(freemarkerInput, stringWriter);
        } catch (IOException | TemplateException exception) {
            final var camelRouteId = (String) freemarkerInput.get("routeId");

            if (log.isWarnEnabled()) {
                log.warn("An error occurred while populating template. Failed to create "
                                + "Camel route for route. [routeId=({}), exception=({})]",
                        camelRouteId, exception.getMessage());
            }

            throw exception;
        }

        return stringWriter;
    }

    /**
     * Deletes the Camel route for a given {@link Route}. The route is stopped at and removed
     * from the Camel context.
     *
     * @param route the route.
     * @throws RouteDeletionException if the Camel route cannot be deleted.
     */
    public void deleteRoute(final Route route) {
        final var camelRouteId = route.getId().toString();
        deleteRouteById(camelRouteId);
    }

    /**
     * Deletes a Camel route by id. The route is stopped at and removed from the Camel context.
     *
     * @param camelRouteId the id of the Camel route.
     * @throws RouteDeletionException if the Camel route cannot be deleted.
     */
    private void deleteRouteById(final String camelRouteId) {
        if (camelContext.getRoute(camelRouteId) != null) {
            try {
                camelContext.getRouteController().stopRoute(camelRouteId);
                if (!camelContext.removeRoute(camelRouteId)) {
                    throw new IllegalStateException("Could not remove route because route was not "
                            + "stopped.");
                }
                if (log.isDebugEnabled()) {
                    log.debug("Removed route from Camel context. [routeId=({})]", camelRouteId);
                }
            } catch (Exception e) {
                throw new RouteDeletionException("Error deleting Camel route for AppRoute with ID '"
                        + camelRouteId + "'", e);
            }
        }
    }

    /**
     * Generates the id of the Camel route for a given {@link AppRoute}. The Camel route id is the
     * UUID from the AppRoute's id.
     *
     * @param appRoute the AppRoute.
     * @return the Camel route id.
     */
    private String getCamelRouteId(final AppRoute appRoute) {
        return UUID.fromString(appRoute.getId()
                .toString()
                .split("/")[appRoute.getId().toString().split("/").length - 1])
                .toString();
    }

}
