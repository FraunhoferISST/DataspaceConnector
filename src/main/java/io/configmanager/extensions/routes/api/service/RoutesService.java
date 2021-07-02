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
package io.configmanager.extensions.routes.api.service;

import de.fraunhofer.iais.eis.AppRoute;
import de.fraunhofer.iais.eis.AppRouteImpl;
import de.fraunhofer.iais.eis.Endpoint;
import de.fraunhofer.iais.eis.GenericEndpoint;
import de.fraunhofer.iais.eis.RouteStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

/**
 * Service class for managing app routes in the configuration manager.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoutesService {
    /**
     * This method creates an app route.
     *
     * @param description description of the app route
     * @return app route
     */
    public AppRoute createAppRoute(final String description) {
        //TODO: Save to DB

//        final var configModelImpl = (ConfigurationModelImpl) configModelService.getConfigModel();
//
//        if (configModelService.getConfigModel().getAppRoute() == null) {
//            configModelImpl.setAppRoute(new ArrayList<>());
//        }
//
//        final var appRoutes = (ArrayList<AppRoute>) configModelImpl.getAppRoute();
//        final var routeDeployMethod = routeDeployMethodRepository.findAll();
//        String deployMethod;
//
//        if (routeDeployMethod.isEmpty()) {
//            deployMethod = "custom";
//        } else {
//            deployMethod = routeDeployMethod.get(0).getRouteDeployMethod().toString();
//        }
//
//        final var appRoute = new AppRouteBuilder()
//                ._routeDeployMethod_(deployMethod)
//                ._routeDescription_(description)
//                .build();
//
//        appRoutes.add(appRoute);
//        configModelImpl.setAppRoute(appRoutes);
//
//        configModelService.saveState();
//
//        return appRoute;

        return null;
    }

    /**
     * This method deletes an app route.
     *
     * @param routeId id of the app route
     * @return true, if app route is deleted
     */
    public boolean deleteAppRoute(final URI routeId) {
        //TODO: Delete in DB

//        var deleted = false;
//
//        final var appRoute = getAppRoute(routeId);
//
//        if (appRoute != null) {
//            deleted = configModelService.getConfigModel().getAppRoute().remove(appRoute);
//
//            if (deleted) {
//                try {
//                    routeManager.deleteRoute(appRoute);
//                } catch (RouteDeletionException e) {
//                    if (log.isErrorEnabled()){
//                        log.error(e.getMessage(), e);
//                    }
//                }
//                configModelService.saveState();
//            }
//        }
//
//        return deleted;

        return true;
    }

    /**
     * This method returns an app route.
     *
     * @param routeId id of the app route
     * @return app route
     */
    public AppRoute getAppRoute(final URI routeId) {
        //TODO: Get from DB

//        return configModelService.getConfigModel().getAppRoute()
//                .stream()
//                .filter(appRoute1 -> appRoute1.getId().equals(routeId)).findAny().orElse(null);

        return null;
    }

    /**
     * @return list of app routes
     */
    public List<AppRoute> getAppRoutes() {
        //TODO: Get from DB

//        return configModelService.getConfigModel().getAppRoute();

        return null;
    }

    /**
     * This method returns a specific app route with the given parameter.
     *
     * @param routeId id of the route
     * @return app route implementation
     */
    private AppRouteImpl getAppRouteImpl(final URI routeId) {
        //TODO: Get from DB

//        return (AppRouteImpl) configModelService.getConfigModel().getAppRoute()
//                .stream()
//                .filter(appRoute -> appRoute.getId().equals(routeId)).findAny().orElse(null);

        return null;
    }

    /**
     * Creates an AppRoute step.
     * @param routeId The ID of the route, where the step should be added.
     * @param startId ID of the Start-Endpoint.
     * @param endID ID of the End-Endpoint.
     * @param resourceId Resource for the route.
     * @param startCoordinateX GUI: X-Coordinate of the Start-Endpoint.
     * @param startCoordinateY GUI: Y-Coordinate of the Start-Endpoint.
     * @param endCoordinateX GUI: X-Coordinate of the End-Endpoint.
     * @param endCoordinateY GUI: Y-Coordinate of the End-Endpoint.
     * @return The created RouteStep.
     */
    public RouteStep createAppRouteStep(final URI routeId,
                                        final URI startId,
                                        final URI endID,
                                        final URI resourceId,
                                        final int startCoordinateX,
                                        final int startCoordinateY,
                                        final int endCoordinateX,
                                        final int endCoordinateY) {

        //TODO: Save in DB

//        RouteStep routeStep = null;
//        // Create and save the endpoints of the route with the respective coordinates
//        final var startEndpointInformation =
//                new EndpointInformation(routeId.toString(), startId.toString(), startCoordinateX,
//                        startCoordinateY);
//
//        final var endEndpointInformation =
//                new EndpointInformation(routeId.toString(), endID.toString(), endCoordinateX,
//                        endCoordinateY);
//
//        endpointInformationRepository.save(startEndpointInformation);
//        endpointInformationRepository.save(endEndpointInformation);
//
//        final var appRouteImpl = getAppRouteImpl(routeId);
//        if (appRouteImpl != null) {
//
//            if (appRouteImpl.getHasSubRoute() == null) {
//                appRouteImpl.setHasSubRoute(new ArrayList<>());
//            }
//            final var routeSteps = (ArrayList<RouteStep>) appRouteImpl.getHasSubRoute();
//
//            // Determine endpoints
//            final var startEndpoint = getEndpoint(startId);
//            final var endpoint = getEndpoint(endID);
//
//            // Set app route start and end
//            if (routeSteps.isEmpty()) {
//                appRouteImpl.setAppRouteStart(Util.asList(startEndpoint));
//            }
//            appRouteImpl.setAppRouteEnd(Util.asList(endpoint));
//
//            // Get route deploy method for route step
//            final var routeDeployMethod = routeDeployMethodRepository.findAll();
//
//            String deployMethod;
//            if (routeDeployMethod.isEmpty()) {
//                deployMethod = "custom";
//            } else {
//                deployMethod = routeDeployMethod.get(0).getRouteDeployMethod().toString();
//            }
//
//            // Create route step
//            if (startEndpoint != null && endpoint != null) {
//                final var resource = resourceService.getResource(resourceId);
//                if (resource != null) {
//
//                    // Set resource endpoint
//                    if (configModelService.getConfigModel()
//                                .getConnectorDescription().getHasEndpoint() == null
//                            || configModelService.getConfigModel()
//                                .getConnectorDescription().getHasEndpoint().isEmpty()) {
//
//                        final var baseConnectorImpl =
//                                (BaseConnectorImpl) configModelService
//                                        .getConfigModel()
//                                        .getConnectorDescription();
//                        baseConnectorImpl.setHasEndpoint(
//                                Util.asList(new ConnectorEndpointBuilder()
//                                ._accessURL_(URI.create("http://api/ids/data")).build()));
//                    }
//                    final var connectorEndpoint =
//                            configModelService.getConfigModel().getConnectorDescription()
//                                    .getHasEndpoint().get(0);
//                    final var resourceImpl = (ResourceImpl) resource;
//                    resourceImpl.setResourceEndpoint(Util.asList(connectorEndpoint));
//
//                    routeStep = new RouteStepBuilder()._routeDeployMethod_(deployMethod)
//                            ._appRouteStart_(Util.asList(startEndpoint))
//                            ._appRouteEnd_(Util.asList(endpoint))
//                            ._appRouteOutput_(Util.asList(resourceImpl))
//                            .build();
//
//                    // Creating camel route
//                    try {
//                        routeManager.createAndDeployXMLRoute(
//                                configModelService.getConfigModel(), appRouteImpl);
//                    } catch (RouteCreationException e) {
//                        if (log.isErrorEnabled()){
//                            log.error(e.getMessage(), e);
//                        }
//                    }
//                } else {
//                    routeStep = new RouteStepBuilder()._routeDeployMethod_(deployMethod)
//                            ._appRouteStart_(Util.asList(startEndpoint))
//                            ._appRouteEnd_(Util.asList(endpoint))
//                            .build();
//                }
//                routeSteps.add(routeStep);
//                configModelService.saveState();
//            }
//        }
//        return routeStep;

        return null;
    }

    /**
     * This method returns an generic endpoint, app endpoint or a connector endpoint.
     *
     * @param endpointId id of the endpoint
     * @return endpoint
     */
    private Endpoint getEndpoint(final URI endpointId) {
        //TODO: Get from DB

//        Endpoint endpoint = null;
//
//        // Search endpoint in the app repository
//        final var customAppList = customAppRepository.findAll();
//        if (!customAppList.isEmpty() && endpointId.toString().contains("appEndpoint")) {
//            final var customApp = customAppList.stream()
//                    .map(CustomApp::getAppEndpointList)
//                    .flatMap(Collection::stream)
//                    .filter(customAppEndpoint -> customAppEndpoint
//                            .getEndpoint().getId().equals(endpointId))
//                    .findAny().orElse(null);
//
//            if (customApp != null) {
//                endpoint = customApp.getEndpoint();
//            }
//        }
//        // Search endpoint in the backend repository and in list of connector endpoints
//        if (endpoint == null && !endpointService.getGenericEndpoints().isEmpty()
//                && endpointId.toString().contains("genericEndpoint")) {
//            final var genericEndpoint = endpointService.getGenericEndpoint(endpointId);
//
//            if (genericEndpoint != null) {
//                endpoint = genericEndpoint;
//            }
//        }
//
//        if (endpoint == null && !configModelService.getConfigModel()
//                    .getConnectorDescription().getHasEndpoint().isEmpty()
//                && endpointId.toString().contains("connectorEndpoint")) {
//
//            endpoint = configModelService.getConfigModel()
//                    .getConnectorDescription().getHasEndpoint()
//                    .stream().filter(connectorEndpoint -> connectorEndpoint
//                        .getId().equals(endpointId))
//                    .findAny().orElse(null);
//        }
//
//        return endpoint;

        return null;
    }

    /**
     * This method returns an endpoint information.
     *
     * @param routeId    id of the route
     * @param endpointId id of the endpoint
     * @return endpoint information
     */
    public Object getEndpointInformation(final URI routeId, final URI endpointId) {
        //TODO: Get from DB

//        EndpointInformation returnEndpointInfo = null;
//        final var endpointInformations = endpointInformationRepository.findAll();
//
//        if (!endpointInformations.isEmpty()) {
//            for (final var endpointInformation : endpointInformations) {
//                if (routeId.toString().equals(endpointInformation.getRouteId())
//                        && endpointId.toString().equals(endpointInformation.getEndpointId())) {
//                    returnEndpointInfo = endpointInformation;
//                }
//            }
//        }
//
//        return returnEndpointInfo;

        return null;
    }

    /**
     * This method creates a generic endpoint with the given parameters.
     *
     * @param accessURL  access url of the endpoint
     * @param sourceType the source type of the representation
     * @param username   username for the authentication
     * @param password   password for the authentication
     * @return generic endpoint
     */
    public GenericEndpoint createGenericEndpoint(final URI accessURL,
                                                 final String sourceType,
                                                 final String username,
                                                 final String password) {
        //TODO: save in DB
        return null;
    }

    /**
     * @return list of generic endpoints
     */
    public List<Endpoint> getGenericEndpoints() {
        //TODO: get from DB
        return null;
    }

    /**
     * @param id id of the generic endpoint
     * @return generic endpoint
     */
    public GenericEndpoint getGenericEndpoint(final URI id) {
        //TODO: get from DB
        return null;
    }

    /**
     * @param id id of the generic endpoint
     * @return true, if generic endpoint is deleted
     */
    public boolean deleteGenericEndpoint(final URI id) {
        //TODO: delete from DB
        return true;
    }

    /**
     * This method updates a generic endpoint with the given parameters.
     *
     * @param id         id of the generic endpoint
     * @param accessURL  access url of the endpoint
     * @param sourceType the source type of the representation
     * @param username   username for the authentication
     * @param password   password for the authentication
     * @return true, if generic endpoint is updated
     */
    public boolean updateGenericEndpoint(final URI id,
                                         final URI accessURL,
                                         final String sourceType,
                                         final String username,
                                         final String password) {
        //TODO: save in DB
        return true;
    }

}
