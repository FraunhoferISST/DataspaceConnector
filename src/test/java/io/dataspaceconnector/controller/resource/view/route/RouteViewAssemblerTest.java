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
package io.dataspaceconnector.controller.resource.view.route;

import io.dataspaceconnector.model.configuration.DeployMethod;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.model.route.RouteDesc;
import io.dataspaceconnector.model.route.RouteFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RouteViewAssemblerTest {

    @Test
    public void create_ValidRoute_returnRouteView() {
        /* ARRANGE */
        final var shouldLookLike = getRoute();

        /* ACT */
        final var after = getRouteView();

        /* ASSERT */
        assertEquals(after.getDescription(), shouldLookLike.getDescription());
        assertEquals(after.getTitle(), shouldLookLike.getTitle());
        assertEquals(after.getDeploy(), shouldLookLike.getDeploy());
        assertTrue(after.getLink("output").isPresent());
        assertTrue(after.getLink("routes").isPresent());
    }

    private Route getRoute() {
        final var factory = new RouteFactory();
        return factory.create(getRouteDesc());
    }

    private RouteDesc getRouteDesc() {
        final var desc = new RouteDesc();
        desc.setDescription("Route Desc");
        desc.setTitle("My Route");
        desc.setDeploy(DeployMethod.CAMEL);
        return desc;
    }

    private RouteView getRouteView() {
        final var assembler = new RouteViewAssembler();
        return assembler.toModel(getRoute());
    }
}
