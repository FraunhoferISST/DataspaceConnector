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
package io.configmanager.extensions.routes.camel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.Unmarshaller;

import de.fraunhofer.iais.eis.AppEndpoint;
import de.fraunhofer.iais.eis.AppEndpointType;
import de.fraunhofer.iais.eis.AppRoute;
import de.fraunhofer.iais.eis.ConnectorEndpoint;
import de.fraunhofer.iais.eis.Endpoint;
import de.fraunhofer.iais.eis.GenericEndpoint;
import de.fraunhofer.iais.eis.RouteStep;
import io.configmanager.extensions.routes.camel.dto.RouteStepEndpoint;
import io.configmanager.extensions.routes.camel.exceptions.NoSuitableTemplateException;
import io.configmanager.extensions.routes.camel.exceptions.RouteCreationException;
import io.configmanager.extensions.routes.camel.exceptions.RouteDeletionException;
import io.dataspaceconnector.model.route.Route;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.apache.commons.codec.binary.Base64;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

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
    private final @NonNull DefaultCamelContext camelContext;

    /**
     * Creates a Camel XML route from a given app route. The generated XML route is then added to
     * the application's Camel context for execution.
     *
     * @param appRoute the app route to create a Camel route for
     * @throws RouteCreationException if the Camel route cannot be created or deployed
     */
    public void createAndDeployXMLRoute(final AppRoute appRoute) throws RouteCreationException {
        final var velocityContext = new VelocityContext();

        //create ID for Camel route
        final var camelRouteId = getCamelRouteId(appRoute);
        velocityContext.put("routeId", camelRouteId);

        //get route start and end (will either be connector, app or generic endpoint)
        addRouteStartToContext(velocityContext,
                (ArrayList<? extends Endpoint>) appRoute.getAppRouteStart());

        addRouteEndToContext(velocityContext,
                (ArrayList<? extends Endpoint>) appRoute.getAppRouteEnd());

        //get route steps (if any)
        addRouteStepsToContext(velocityContext,
                (ArrayList<? extends RouteStep>) appRoute.getHasSubRoute());

        try {
            createDataspaceConnectorRoute(appRoute, velocityContext);
        } catch (Exception e) {
            throw new RouteCreationException("Error creating Camel route for AppRoute with ID '"
                    + appRoute.getId() + "'", e);
        }
    }

    /**
     * Extracts the URL of the {@link AppRoute}'s start and adds it to the Velocity context.
     *
     * @param velocityContext the Velocity context
     * @param routeStart start of the AppRoute
     */
    private void addRouteStartToContext(final VelocityContext velocityContext,
                                        final ArrayList<? extends Endpoint> routeStart)
            throws RouteCreationException {
        if (routeStart.get(0) instanceof ConnectorEndpoint) {
            final var connectorEndpoint = (ConnectorEndpoint) routeStart.get(0);
            velocityContext.put("startUrl", connectorEndpoint.getAccessURL().toString());
        } else if (routeStart.get(0) instanceof GenericEndpoint) {
            final var genericEndpoint = (GenericEndpoint) routeStart.get(0);
            velocityContext.put("startUrl", genericEndpoint.getAccessURL().toString());
            addBasicAuthHeaderForGenericEndpoint(velocityContext, genericEndpoint);
        } else {
            //TODO app is route start
            throw new RouteCreationException("An app as the route start is not yet supported.");
        }
    }

    /**
     * Extracts the URL of the {@link AppRoute}'s end and adds it to the Velocity context.
     *
     * @param velocityContext the Velocity context
     * @param routeEnd end of the AppRoute
     */
    private void addRouteEndToContext(final VelocityContext velocityContext,
                                      final ArrayList<? extends Endpoint> routeEnd)
            throws RouteCreationException {
        if (routeEnd.get(0) instanceof ConnectorEndpoint) {
            final var connectorEndpoint = (ConnectorEndpoint) routeEnd.get(0);
            velocityContext.put("endUrl", connectorEndpoint.getAccessURL().toString());
        } else if (routeEnd.get(0) instanceof GenericEndpoint) {
            final var genericEndpoint = (GenericEndpoint) routeEnd.get(0);
            velocityContext.put("endUrl", genericEndpoint.getAccessURL().toString());
            addBasicAuthHeaderForGenericEndpoint(velocityContext, genericEndpoint);
        } else {
            //TODO app is route end
            throw new RouteCreationException("An app as the route end is not yet supported.");
        }
    }

    /**
     * Creates and adds the basic authentication header for calling a generic endpoint to a Velocity
     * context, if basic authentication is defined for the given endpoint.
     *
     * @param velocityContext the Velocity context
     * @param genericEndpoint the generic endpoint
     */
    private void addBasicAuthHeaderForGenericEndpoint(final VelocityContext velocityContext,
                                                      final GenericEndpoint genericEndpoint) {
        final var basicAuth = genericEndpoint.getGenericEndpointAuthentication();
        if (basicAuth != null && basicAuth.getAuthUsername() != null
                && !basicAuth.getAuthUsername().isBlank() && basicAuth.getAuthPassword() != null
                && !basicAuth.getAuthPassword().isBlank()) {
            final var username = basicAuth.getAuthUsername();
            final var password = basicAuth.getAuthPassword();
            final var auth = username + ":" + password;
            final var encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
            final var authHeader = "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);
            velocityContext.put("genericEndpointAuthHeader", authHeader);
        }
    }

    /**
     * Extracts the start and end URLs of the {@link AppRoute}'s steps and adds them to the
     * Velocity context.
     *
     * @param velocityContext the Velocity context
     * @param routeSteps steps of the AppRoute
     */
    private void addRouteStepsToContext(final VelocityContext velocityContext,
                                        final ArrayList<? extends RouteStep> routeSteps) {
        final var routeStepEndpoints = new ArrayList<RouteStepEndpoint>();

        if (routeSteps != null) {
            Endpoint lastStepEnd = null;

            for (int i = 0; i < routeSteps.size(); i++) {
                final var routeStep = routeSteps.get(i);

                final var stepStart = routeStep.getAppRouteStart().get(0);

                //if end of last step is same as start of current step only call endpoint once
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

        velocityContext.put("routeStepEndpoints", routeStepEndpoints);
    }

    /**
     * Adds a {@link RouteStepEndpoint} representation of an {@link Endpoint} to the given list.
     *
     * @param endpoint the endpoint.
     * @param list the list.
     */
    private void addRouteStepEndpoint(final Endpoint endpoint, final List<RouteStepEndpoint> list) {
        if (AppEndpoint.class.isAssignableFrom(endpoint.getClass())) {
            final var appEndpoint = (AppEndpoint) endpoint;
            if (appEndpoint.getAppEndpointType() == AppEndpointType.OUTPUT_ENDPOINT) {
                list.add(new RouteStepEndpoint(appEndpoint.getAccessURL(),
                        HttpMethod.GET));
            } else {
                list.add(new RouteStepEndpoint(appEndpoint.getAccessURL(),
                        HttpMethod.POST));
            }
        } else {
            list.add(new RouteStepEndpoint(endpoint.getAccessURL(),
                    HttpMethod.POST));
        }
    }

    /**
     * Creates and deploys a Camel route for the Dataspace Connector. First, Dataspace Connector
     * specific configuration is added to the Velocity Context, which should already contain
     * general route information. Then, the correct route template for the given AppRoute object
     * is chosen from the Dataspace Connector templates. Last, the generated XML route is added to
     * the Camel context.
     *
     * @param appRoute the AppRoute object
     * @param velocityContext the Velocity context
     * @throws Exception if the route file cannot be created or deployed
     */
    private void createDataspaceConnectorRoute(final AppRoute appRoute,
                                               final VelocityContext velocityContext)
            throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("Creating route for Dataspace Connector...");
        }

        //add reference to Camel-Instance's error handler to Velocity context
        velocityContext.put("errorHandlerRef", camelErrorHandlerRef);

        //add basic auth header for connector endpoint
        routeConfigurer.addBasicAuthToContext(velocityContext);

        //choose correct XML template based on route
        final var template = routeConfigurer.getRouteTemplate(appRoute);

        if (template != null) {
            final var velocityEngine = new VelocityEngine();
            velocityEngine.init();

            //populate route template with properties from velocity context to create route
            final var writer = populateTemplate(template, velocityEngine, velocityContext);

            final var inputStream = new ByteArrayInputStream(writer.toString()
                    .getBytes(StandardCharsets.UTF_8));
            final var routes = (RoutesDefinition) unmarshaller.unmarshal(inputStream);
            camelContext.addRouteDefinitions(routes.getRoutes());

        } else {
            if (log.isWarnEnabled()) {
                log.warn("Template is null. Unable to create XML route file for AppRoute"
                        + " with ID '{}'", appRoute.getId());
            }

            throw new NoSuitableTemplateException("No suitable Camel route template found for "
                    + "AppRoute with ID '" + appRoute.getId() + "'");
        }
    }

    /**
     * Populates a given Velocity template using the values from a given Velocity context.
     *
     * @param resource the template
     * @param velocityEngine the Velocity engine required for populating the template
     * @param velocityContext the context containing the values to insert into the template
     * @return the populated template as a string
     * @throws IOException if an error occurs while filling out the route template
     */
    private StringWriter populateTemplate(final Resource resource,
                                          final VelocityEngine velocityEngine,
                                          final VelocityContext velocityContext)
            throws IOException {
        final var stringWriter = new StringWriter();
        InputStreamReader inputStreamReader;

        try {
            inputStreamReader = new InputStreamReader(resource.getInputStream(),
                    StandardCharsets.UTF_8);
            velocityEngine.evaluate(velocityContext, stringWriter, "", inputStreamReader);
        } catch (IOException e) {
            final var camelRouteId = (String) velocityContext.get("routeId");

            if (log.isErrorEnabled()) {
                log.error("An error occurred while populating template."
                                + " Please check all respective files for connection"
                                + " with ID '{}' for correctness! (Error message: {})",
                        camelRouteId, e.toString());
            }

            throw e;
        }

        return stringWriter;
    }

    /**
     * Deletes all Camel routes associated with app routes in a given list by calling
     * {@link RouteManager#deleteRoute(AppRoute)}.
     *
     * @param appRoutes the list of app routes to be deleted.
     * @throws RouteDeletionException if any of the Camel routes cannot be deleted
     */
    public void deleteRouteFiles(final List<AppRoute> appRoutes)
            throws RouteDeletionException {
        for (final var appRoute: appRoutes) {
            deleteRoute(appRoute);
        }
    }

    /**
     * Deletes the Camel route for a given {@link AppRoute}. The route is stopped at and removed
     * from the Camel context.
     *
     * @param appRoute the AppRoute
     * @throws RouteDeletionException if the Camel route cannot be deleted
     */
    public void deleteRoute(final AppRoute appRoute) throws RouteDeletionException {
        final var appRouteId = UUID.fromString(appRoute.getId().toString()
                .split("/")[appRoute.getId().toString().split("/").length - 1]);
        final var camelRouteId = getCamelRouteId(appRouteId);
        deleteRouteById(camelRouteId);
    }

    /**
     * Deletes the Camel route for a given {@link Route}. The route is stopped at and removed
     * from the Camel context.
     *
     * @param route the route
     * @throws RouteDeletionException if the Camel route cannot be deleted
     */
    public void deleteRoute(final Route route) {
        final var camelRouteId = getCamelRouteId(route.getId());
        deleteRouteById(camelRouteId);
    }

    /**
     * Deletes a Camel route by ID. The route is stopped at and removed from the Camel context.
     *
     * @param camelRouteId the ID of the Camel route.
     * @throws RouteDeletionException if the Camel route cannot be deleted
     */
    private void deleteRouteById(final String camelRouteId) {
        if (camelContext.getRoute(camelRouteId) != null) {
            try {
                camelContext.stopRoute(camelRouteId);
                if (!camelContext.removeRoute(camelRouteId)) {
                    throw new IllegalStateException("Could not remove route because route was not "
                            + "stopped.");
                }
            } catch (Exception e) {
                throw new RouteDeletionException("Error deleting Camel route for AppRoute with ID '"
                        + camelRouteId + "'", e);
            }
        }
    }

    /**
     * Generates the ID of the Camel route for a given {@link AppRoute}. The Camel route ID consists
     * of the String 'app-route_' followed by the UUID from the AppRoute's ID.
     *
     * @param appRoute the AppRoute.
     * @return the Camel route ID
     */
    private String getCamelRouteId(final AppRoute appRoute) {
        final var appRouteId = UUID.fromString(appRoute.getId().toString()
                .split("/")[appRoute.getId().toString().split("/").length - 1]);
        return getCamelRouteId(appRouteId);
    }

    /**
     * Generates the ID of the Camel route for a given UUID. The Camel route ID consists
     * of the String 'app-route_' followed by the route's UUID.
     *
     * @param uuid the uuid of the route.
     * @return the Camel route ID
     */
    private String getCamelRouteId(final UUID uuid) {
        return "app-route_" + uuid;
    }

}
