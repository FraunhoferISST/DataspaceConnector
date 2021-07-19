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
package io.dataspaceconnector.service.configuration;

import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.broker.Broker;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.service.resource.ArtifactService;
import io.dataspaceconnector.service.resource.OfferedResourceService;
import io.dataspaceconnector.service.resource.OwningRelationService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains all implementations of {@link OwningRelationService}.
 */
public final class EntityLinkerService {

    /**
     * Handles the relation between broker and offered resources.
     */
    @Service
    @NoArgsConstructor
    public static class BrokerOfferedResourcesLinker extends OwningRelationService<Broker,
            OfferedResource, BrokerService, OfferedResourceService> {

        @Override
        protected final List<OfferedResource> getInternal(final Broker owner) {
            return owner.getOfferedResources();
        }
    }

    /**
     * Handles the relation between the routes and sub-routes.
     */
    @Service
    @NoArgsConstructor
    public static class RouteStepsLinker extends OwningRelationService<Route, Route, RouteService,
            RouteService> {

        @Override
        public final List<Route> getInternal(final Route owner) {
            return owner.getSteps();
        }
    }

    /**
     * Handles the relation between the route and offered resources.
     */
    @Service
    @NoArgsConstructor
    public static class RouteArtifactsLinker extends OwningRelationService<Route, Artifact,
            RouteService, ArtifactService> {

        @Override
        protected final List<Artifact> getInternal(final Route owner) {
            return owner.getOutput();
        }
    }
}
