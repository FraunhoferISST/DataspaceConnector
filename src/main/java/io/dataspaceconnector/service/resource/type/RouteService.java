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
package io.dataspaceconnector.service.resource.type;

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.RouteCreationException;
import io.dataspaceconnector.common.exception.RouteDeletionException;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.model.route.RouteDesc;
import io.dataspaceconnector.model.route.RouteFactory;
import io.dataspaceconnector.repository.BaseEntityRepository;
import io.dataspaceconnector.repository.EndpointRepository;
import io.dataspaceconnector.repository.RouteRepository;
import io.dataspaceconnector.service.resource.base.BaseEntityService;
import io.dataspaceconnector.service.resource.relation.RouteStepLinker;
import io.dataspaceconnector.service.routing.RouteHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

/**
 * Service class for routes.
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.NONE)
public class RouteService extends BaseEntityService<Route, RouteDesc> {

    /**
     * Repository for storing data.
     **/
    private final @NonNull EndpointRepository endpointRepo;

    /**
     * Service for all endpoints.
     */
    private final @NonNull EndpointServiceProxy endpointService;

    /**
     * Helper class for deploying and deleting Camel routes.
     */
    private final @NonNull RouteHelper routeHelper;

    /**
     * Constructor for route service.
     *
     * @param repository           The route repository.
     * @param factory              The route factory.
     * @param endpointRepository   The endpoint repository.
     * @param endpointServiceProxy The endpoint service.
     * @param camelRouteHelper     The helper class for Camel routes.
     */
    public RouteService(
            final BaseEntityRepository<Route> repository,
            final AbstractFactory<Route, RouteDesc> factory,
            final @NonNull EndpointRepository endpointRepository,
            final @NonNull EndpointServiceProxy endpointServiceProxy,
            final @NonNull RouteHelper camelRouteHelper) {
        super(repository, factory);
        this.endpointRepo = endpointRepository;
        this.endpointService = endpointServiceProxy;
        this.routeHelper = camelRouteHelper;
    }

    @Override
    protected final Route persist(final Route route) throws RouteCreationException {
        if (route.getStart() != null) {
            endpointRepo.save(route.getStart());
        }
        if (route.getEnd() != null) {
            endpointRepo.save(route.getEnd());
        }

        var repo = (RouteRepository) getRepository();
        if (repo.findAllTopLevelRoutes().contains(route)) {
            routeHelper.deploy(route);
        }

        return super.persist(route);
    }

    /**
     * Set the start point of a route.
     *
     * @param routeId    The route id.
     * @param endpointId The endpoint id.
     */
    public void setStartEndpoint(final UUID routeId, final UUID endpointId) {
        persist(((RouteFactory) getFactory()).setStartEndpoint(get(routeId),
                endpointService.get(endpointId)));
    }

    /**
     * Remove the start point of a route.
     *
     * @param routeId The route id.
     */
    public void removeStartEndpoint(final UUID routeId) {
        persist(((RouteFactory) getFactory()).deleteStartEndpoint(get(routeId)));
    }

    /**
     * Set the last point of a route.
     *
     * @param routeId    The route id.
     * @param endpointId The endpoint id.
     */
    public void setLastEndpoint(final UUID routeId, final UUID endpointId) {
        persist(((RouteFactory) getFactory()).setLastEndpoint(get(routeId),
                endpointService.get(endpointId)));
    }

    /**
     * Remove the last point of a route.
     *
     * @param routeId The route id.
     */
    public void removeLastEndpoint(final UUID routeId) {
        persist(((RouteFactory) getFactory()).deleteLastEndpoint(get(routeId)));
    }

    /**
     * Delete a route with the given id, after the associated Camel route has been successfully
     * deleted.
     *
     * @param routeId The id of the entity.
     * @throws IllegalArgumentException if the passed id is null.
     */
    @Override
    public void delete(final UUID routeId) throws RouteDeletionException {
        Utils.requireNonNull(routeId, ErrorMessage.ENTITYID_NULL);

        final var route = get(routeId);
        routeHelper.delete(route);

        final var linker = new RouteStepLinker();
        final var steps = linker.getInternal(route);
        if (steps != null && !steps.isEmpty()) {
            setStartEndpoint(routeId, steps.get(0).getStart().getId());
            setLastEndpoint(routeId, steps.get(0).getEnd().getId());
        }
        ((RouteFactory) getFactory()).deleteSubroutes(route);

        super.delete(routeId);
    }

}
