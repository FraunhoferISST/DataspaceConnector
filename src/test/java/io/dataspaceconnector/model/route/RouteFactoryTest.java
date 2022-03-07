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
package io.dataspaceconnector.model.route;

import io.dataspaceconnector.common.exception.InvalidEntityException;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.configuration.DeployMethod;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteFactoryTest {

    final RouteDesc desc = new RouteDesc();
    final RouteFactory factory = new RouteFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        final var title = "MyRoute";
        final var description = "MyDesc";
        desc.setTitle(title);
        desc.setDescription(description);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(title, result.getTitle());
        assertEquals(description, result.getDescription());
        assertNull(result.getOutput());
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

    @Test
    void update_sameRouteConfig_willNotUpdate() {
        /* ARRANGE */
        final var desc = new RouteDesc();
        final var route = factory.create(new RouteDesc());

        /* ACT */
        final var result = factory.update(route, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(RouteFactory.DEFAULT_CONFIGURATION, route.getConfiguration());
    }


    @Test
    void update_newDeployMethod_willUpdate() {
        /* ARRANGE */
        final var desc = new RouteDesc();
        desc.setDeploy(DeployMethod.CAMEL);
        final var route = factory.create(new RouteDesc());

        /* ACT */
        final var result = factory.update(route, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getDeploy(), route.getDeploy());
    }

    @Test
    void update_deployMethodNoneButLinkedToArtifact_throwInvalidEntityException() {
        /* ARRANGE */
        final var desc = new RouteDesc();
        desc.setDeploy(DeployMethod.CAMEL);
        final var route = factory.create(desc);
        ReflectionTestUtils.setField(route, "output", new ArtifactImpl());
        final var newDesc = new RouteDesc();

        /* ACT && ASSERT */
        assertThrows(InvalidEntityException.class, () -> factory.update(route, newDesc));
    }

    @Test
    void update_sameDeployMethod_willNotUpdate() {
        /* ARRANGE */
        final var desc = new RouteDesc();
        final var route = factory.create(new RouteDesc());

        /* ACT */
        final var result = factory.update(route, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(DeployMethod.NONE, route.getDeploy());
    }

    @Test
    void setStartEndpoint_setValue_willUpdate() {
        final var route = new Route();
        final var endpoint = new GenericEndpoint();

        assertEquals(endpoint, factory.setStartEndpoint(route, endpoint).getStart());
    }

    @Test
    void removeStartEndpoint_willRemove() {
        final var route = new Route();
        route.setStart(new GenericEndpoint());

        assertNull(factory.deleteStartEndpoint(route).getStart());
    }

    @Test
    void removeStartEndpoint_outputNotNull_throwInvalidEntityException() {
        /* ARRANGE */
        final var route = new Route();
        final var artifact = new ArtifactImpl();
        ReflectionTestUtils.setField(route, "output", artifact);
        route.setStart(new GenericEndpoint());

        /* ACT && ASSERT */
        assertThrows(InvalidEntityException.class, () -> factory.deleteStartEndpoint(route));
    }

    @Test
    void setLastEndpoint_setValue_willUpdate() {
        final var route = new Route();
        final var endpoint = new GenericEndpoint();

        assertEquals(endpoint, factory.setLastEndpoint(route, endpoint).getEnd());
    }

    @Test
    void removeLastEndpoint_willRemove() {
        final var route = new Route();
        route.setEnd(new GenericEndpoint());

        assertNull(factory.deleteLastEndpoint(route).getEnd());
    }

    @Test
    void deleteSubroutes_returnEmpty() {
        /* ARRANGE */
        final var route = factory.create(new RouteDesc());
        route.setSteps(new ArrayList<>());

        /* ACT */
        factory.deleteSubroutes(route);

        /* ASSERT */
        assertNull(route.getSteps());
    }

    @Test
    void setOutput_returnRouteWithOutput() {
        /* ARRANGE */
        final var route = factory.create(new RouteDesc());
        final var artifact = new ArtifactImpl();

        /* ACT */
        final var result = factory.setOutput(route, artifact);

        /* ASSERT */
        assertEquals(artifact, result.getOutput());
    }

    @Test
    void deleteOutput_returnRouteWithoutOutput() {
        /* ARRANGE */
        final var route = factory.create(new RouteDesc());
        final var artifact = new ArtifactImpl();
        ReflectionTestUtils.setField(route, "output", artifact);

        /* ACT */
        final var result = factory.deleteOutput(route);

        /* ASSERT */
        assertNull(result.getOutput());
    }
}
