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
package io.dataspaceconnector.services.configuration;

import java.util.List;

import io.dataspaceconnector.model.apps.App;
import io.dataspaceconnector.model.appstore.AppStore;
import io.dataspaceconnector.model.broker.Broker;
import io.dataspaceconnector.model.endpoints.Endpoint;
import io.dataspaceconnector.model.resources.OfferedResource;
import io.dataspaceconnector.model.routes.Route;
import io.dataspaceconnector.services.resources.EndpointServiceProxy;
import io.dataspaceconnector.services.resources.OfferedResourceService;
import io.dataspaceconnector.services.resources.OwningRelationService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * This class contains all implementations of {@link OwningRelationService}.
 */
public final class EntityLinkerService {

    /**
     * Handles the relation between app store and apps.
     */
    @Service
    @NoArgsConstructor
    public static class AppStoreAppLinker
            extends OwningRelationService<AppStore, App, AppStoreService, AppService> {

        @Override
        protected final List<App> getInternal(final AppStore owner) {
            return owner.getAppList();
        }
    }

    /**
     * Handles the relation between broker and offered resources.
     */
    @Service
    @NoArgsConstructor
    public static class BrokerOfferedResourcesLinker
            extends OwningRelationService<Broker, OfferedResource, BrokerService,
            OfferedResourceService> {

        @Override
        protected final List<OfferedResource> getInternal(final Broker owner) {
            return owner.getOfferedResources();
        }
    }

    /**
     * Handles the relation between the routes and subroutes.
     */
    @Service
    @NoArgsConstructor
    public static class RouteStepsLinker
            extends OwningRelationService<Route, Route, RouteService, RouteService> {

        @Override
        protected final List<Route> getInternal(final Route owner) {
            return owner.getSteps();
        }
    }

    /**
     * Handles the relation between the route and offered resources.
     */
    @Service
    @NoArgsConstructor
    public static class RouteOfferedResourceLinker
            extends OwningRelationService<Route, OfferedResource, RouteService,
            OfferedResourceService> {

        @Override
        protected final List<OfferedResource> getInternal(final Route owner) {
            throw new RuntimeException("Not implemented");
        }
    }

    /**
     * Handles the relation between the route and start endpoint.
     */
    @Service
    @NoArgsConstructor
    public static class RouteEndpointLinker
            extends OwningRelationService<Route, Endpoint, RouteService,
            EndpointServiceProxy> {

        @Override
        protected final List<Endpoint> getInternal(final Route owner) {
            throw new RuntimeException("Not implemented");
        }
    }
}
