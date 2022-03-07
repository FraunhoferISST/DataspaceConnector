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
package io.dataspaceconnector.repository;

import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.route.Route;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for routes.
 */
@Repository
public interface RouteRepository extends BaseEntityRepository<Route> {

    /**
     * Returns all routes that are not sub-routes of other routes.
     *
     * @return all top-level routes.
     */
    @Query("SELECT r FROM Route r "
            + "WHERE r NOT IN "
            + "(SELECT DISTINCT r2 FROM Route r3 JOIN r3.steps r2)")
    List<Route> findAllTopLevelRoutes();

    /**
     * Returns all top-level routes that either reference a given endpoint directly or contain a
     * sub-route that does so.
     *
     * @param endpointId ID of the endpoint.
     * @return list of all top-level routes containing the endpoint.
     */
    @Query("SELECT r FROM Route r "
            + "WHERE (r NOT IN "
            + "(SELECT DISTINCT r2 FROM Route r3 JOIN r3.steps r2)"
            + "AND r.start.id = :endpointId OR r.end.id = :endpointId)"
            + "OR r IN "
            + "(SELECT r FROM Route r JOIN Route r2 ON r2 MEMBER OF r.steps "
            + "WHERE r2.start.id = :endpointId OR r2.end.id = :endpointId)")
    List<Route> findTopLevelRoutesByEndpoint(UUID endpointId);

    /**
     * Returns the route associated with a given artifact.
     *
     * @param artifact the artifact.
     * @return the associated route.
     */
    Route findRouteByOutput(Artifact artifact);
}
