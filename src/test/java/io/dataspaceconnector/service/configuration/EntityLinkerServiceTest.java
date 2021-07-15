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

import io.dataspaceconnector.model.broker.Broker;
import io.dataspaceconnector.model.route.Route;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class EntityLinkerServiceTest {

    @Autowired
    private EntityLinkerService.BrokerOfferedResourcesLinker brokerOfferedResourcesLinker;

    @Autowired
    private EntityLinkerService.RouteStepsLinker routeStepsLinker;

    @Autowired
    private EntityLinkerService.RouteArtifactsLinker routeArtifactsLinker;

    @Test
    public void brokerOfferedResourcesLinker_getInternal_returnBrokerOfferedResources() {
        /* ARRANGE */
        /* ACT && ASSERT */
        assertNull(brokerOfferedResourcesLinker.getInternal(new Broker()));
    }

    @Test
    public void routeStepsLinker_getInternal_returnBrokerSteps() {
        /* ARRANGE */
        /* ACT && ASSERT */
        assertNull(routeStepsLinker.getInternal(new Route()));
    }

    @Test
    public void routeArtifactsLinker_getInternal_returnBrokerOfferedResources() {
        /* ARRANGE */
        /* ACT && ASSERT */
        assertNull(routeArtifactsLinker.getInternal(new Route()));
    }
}
