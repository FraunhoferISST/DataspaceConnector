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
package io.dataspaceconnector.controller.resource.view.endpoint;


import io.dataspaceconnector.controller.resource.type.EndpointController;
import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import io.dataspaceconnector.model.endpoint.GenericEndpointDesc;
import io.dataspaceconnector.model.endpoint.GenericEndpointFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenericEndpointViewAssemblerTest {

    @Test
    public void create_ValidGenericEndpoint_returnGenericEndpointView() {
        /* ARRANGE */
        final var shouldLookLike = getGenericEndpoint();
        final var link = new SelfLinkHelper().
                getSelfLink(shouldLookLike.getId(), EndpointController.class);

        /* ACT */
        final var after = getGenericEndpointView();

        /* ASSERT */
        assertEquals(after.getLocation(), shouldLookLike.getLocation());
        assertFalse(after.getLinks().isEmpty());
        assertTrue(after.getLinks().contains(link));
    }

    private GenericEndpoint getGenericEndpoint() {
        final var factory = new GenericEndpointFactory();
        return factory.create(getGenericEndpointDesc());
    }

    private GenericEndpointDesc getGenericEndpointDesc() {
        final var desc = new GenericEndpointDesc();
        desc.setLocation("https://backend.com");
        return desc;
    }

    private GenericEndpointView getGenericEndpointView() {
        final var assembler = new GenericEndpointViewAssembler();
        return assembler.toModel(getGenericEndpoint());
    }
}
