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
package io.dataspaceconnector.service.resource.ids.builder;

import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

import de.fraunhofer.iais.eis.AppRoute;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.common.net.ApiReferenceHelper;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.artifact.RemoteData;
import io.dataspaceconnector.model.auth.BasicAuth;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.configuration.DeployMethod;
import io.dataspaceconnector.model.datasource.DataSource;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import io.dataspaceconnector.model.route.Route;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {IdsAppRouteBuilder.class, IdsRouteStepBuilder.class,
        IdsEndpointBuilder.class, IdsArtifactBuilder.class, ApiReferenceHelper.class})
public class IdsAppRouteBuilderTest {

    @Autowired
    private IdsAppRouteBuilder builder;

    @Autowired
    private ApiReferenceHelper apiReferenceHelper;

    @MockBean
    private SelfLinkHelper selfLinkHelper;

    private final URI endpointDocumentation = URI.create("https://documentation.com");

    private final UUID uuid = UUID.randomUUID();

    @BeforeEach
    void init() {
        final var uri = URI.create("https://" + uuid);
        when(selfLinkHelper.getSelfLink(any(Entity.class))).thenReturn(uri);
    }

    @Test
    void create_inputNull_throwNullPointerException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> builder.create(null));
    }

    @Test
    void create_noSubRoute_returnCompleteAppRoute() {
        /* ARRANGE */
        final var route = getRoute();

        /* ACT */
        final var appRoute = builder.create(route);

        /* ASSERT */
        assertTrue(appRoute.getId().isAbsolute());

        compareRoutes(route, appRoute);
    }

    @Test
    void create_withSubRoute_returnCompleteAppRoute() {
        /* ARRANGE */
        final var route = getRouteWithSubRoute();

        /* ACT */
        final var appRoute = builder.create(route);

        /* ASSERT */
        assertTrue(appRoute.getId().isAbsolute());

        compareRoutes(route, appRoute);
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private void compareRoutes(final Route route, final AppRoute appRoute) {
        assertTrue(appRoute.getId().toString().contains(route.getId().toString()));

        assertEquals(appRoute.getRouteDeployMethod(), route.getDeploy().toString());
        assertEquals(appRoute.getRouteConfiguration(), route.getConfiguration());
        assertEquals(appRoute.getRouteDescription(), route.getDescription());

        assertFalse(appRoute.getAppRouteStart().isEmpty());
        final var start = route.getStart();
        final var appRouteStart = appRoute.getAppRouteStart().get(0);
        compareEndpoints(route, start, appRouteStart);

        assertFalse(appRoute.getAppRouteEnd().isEmpty());
        final var end = route.getEnd();
        final var appRouteEnd = appRoute.getAppRouteEnd().get(0);
        compareEndpoints(route, end, appRouteEnd);

        final var steps = route.getSteps();
        final var subRoutes = appRoute.getHasSubRoute();
        if (steps != null) {
            assertEquals(subRoutes.size(), steps.size());
            for (int i = 0; i < steps.size(); i++) {
                final var step = steps.get(i);
                final var subRoute = subRoutes.get(i);

                assertTrue(subRoute.getId().toString().contains(step.getId().toString()));
                assertEquals(subRoute.getRouteDescription(), step.getDescription());
                assertEquals(subRoute.getRouteDeployMethod(), step.getDeploy().toString());

                assertFalse(subRoute.getAppRouteStart().isEmpty());
                final var subRouteStart = subRoute.getAppRouteStart().get(0);
                compareEndpoints(step, step.getStart(), subRouteStart);

                assertFalse(subRoute.getAppRouteEnd().isEmpty());
                final var subRouteEnd = subRoute.getAppRouteEnd().get(0);
                compareEndpoints(step, step.getEnd(), subRouteEnd);
            }
        }
    }

    private void compareEndpoints(final Route route, final Endpoint endpoint,
                                  final de.fraunhofer.iais.eis.Endpoint idsEndpoint) {
        if (endpoint instanceof GenericEndpoint) {
            compareGenericEndpoints((GenericEndpoint) endpoint,
                    (de.fraunhofer.iais.eis.GenericEndpoint) idsEndpoint);
        } else {
            final var artifact = route.getOutput();
            final var url = apiReferenceHelper.getDataUri(artifact);
            assertEquals(url, idsEndpoint.getAccessURL());
        }
    }

    private void compareGenericEndpoints(final GenericEndpoint endpoint,
                                         final de.fraunhofer.iais.eis.GenericEndpoint idsEndpoint) {
        assertEquals(idsEndpoint.getPath(), endpoint.getLocation());
        assertEquals(idsEndpoint.getEndpointDocumentation().get(0), endpoint.getDocs());
        assertEquals(idsEndpoint.getEndpointInformation().get(0).getValue(), endpoint.getInfo());

        final var auth = (BasicAuth) endpoint.getDataSource().getAuthentication();
        final var idsAuth = idsEndpoint.getGenericEndpointAuthentication();
        assertEquals(idsAuth.getAuthUsername(), auth.getUsername());
        assertEquals(idsAuth.getAuthPassword(), auth.getPassword());
    }

    private Route getRoute() {
        final var route = new Route();
        ReflectionTestUtils.setField(route, "id", uuid);
        ReflectionTestUtils.setField(route, "deploy", DeployMethod.CAMEL);
        ReflectionTestUtils.setField(route, "configuration", "config");
        ReflectionTestUtils.setField(route, "description", "desc");
        ReflectionTestUtils.setField(route, "additional", new HashMap<>());

        final var start = getGenericEndpoint();
        final var artifact = getArtifact();

        ReflectionTestUtils.setField(route, "start", start);
        ReflectionTestUtils.setField(route, "output", artifact);

        return route;
    }

    private Route getRouteWithSubRoute() {
        final var route = getRoute();
        final var subRoute = getSubRoute();
        ReflectionTestUtils.setField(route, "steps", Util.asList(subRoute));

        return route;
    }

    private Route getSubRoute() {
        final var route = new Route();
        ReflectionTestUtils.setField(route, "id", uuid);
        ReflectionTestUtils.setField(route, "deploy", DeployMethod.CAMEL);
        ReflectionTestUtils.setField(route, "configuration", "sub-route-config");
        ReflectionTestUtils.setField(route, "description", "sub-route-desc");
        ReflectionTestUtils.setField(route, "additional", new HashMap<>());

        final var start = getGenericEndpoint();
        final var artifact = getArtifact();

        ReflectionTestUtils.setField(route, "start", start);
        ReflectionTestUtils.setField(route, "output", artifact);

        return route;
    }

    private GenericEndpoint getGenericEndpoint() {
        final var auth = new BasicAuth("", "");
        ReflectionTestUtils.setField(auth, "username", "username");
        ReflectionTestUtils.setField(auth, "password", "password");

        final var dataSource = new DataSource();
        ReflectionTestUtils.setField(dataSource, "authentication", auth);

        final var endpoint = new GenericEndpoint();
        ReflectionTestUtils.setField(endpoint, "id", uuid);
        ReflectionTestUtils.setField(endpoint, "location", "https://location.com");
        ReflectionTestUtils.setField(endpoint, "docs", endpointDocumentation);
        ReflectionTestUtils.setField(endpoint, "info", "info");
        ReflectionTestUtils.setField(endpoint, "dataSource", dataSource);
        ReflectionTestUtils.setField(endpoint, "additional", new HashMap<>());

        return endpoint;
    }

    @SneakyThrows
    private Artifact getArtifact() {
        final var data = new RemoteData();
        ReflectionTestUtils.setField(data, "accessUrl", new URL("https://data"));

        final var artifact = new ArtifactImpl();
        ReflectionTestUtils.setField(artifact, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(artifact, "data", data);
        ReflectionTestUtils.setField(artifact, "creationDate", ZonedDateTime.now());
        ReflectionTestUtils.setField(artifact, "additional", new HashMap<>());

        return artifact;
    }

}
