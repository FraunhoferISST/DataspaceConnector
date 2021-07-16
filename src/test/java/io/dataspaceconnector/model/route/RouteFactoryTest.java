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
package io.dataspaceconnector.model.route;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RouteFactoryTest {

    final RouteDesc desc = new RouteDesc();
    final RouteFactory factory = new RouteFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        final var title = "MyRoute";
        final var description = "MyDesc";
        final var routeType = RouteType.ROUTE;
        desc.setTitle(title);
        desc.setDescription(description);
        desc.setRouteType(routeType);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(title, result.getTitle());
        assertEquals(description, result.getDescription());
        assertEquals(routeType, result.getRouteType());
        assertTrue(result.getOutput().isEmpty());
    }

    @Test
    void update_newRouteConfig_willUpdate() {
        /* ARRANGE */
        final var desc = new RouteDesc();
        desc.setConfiguration("config");
        final var route = factory.create(new RouteDesc());

        /* ACT */
        final var result = factory.update(route, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getConfiguration(), route.getConfiguration());
    }
}
