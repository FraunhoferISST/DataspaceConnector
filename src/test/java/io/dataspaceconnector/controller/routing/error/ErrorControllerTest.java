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
package io.dataspaceconnector.controller.routing.error;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the configmanager RoutesController class.
 */
@SpringBootTest(classes = {ErrorController.class})
@AutoConfigureMockMvc
class ErrorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ErrorController errorController;

    @Test
    void unauthorizedGetRouteErrors() throws Exception {
        mockMvc.perform(get("/api/routes/error")).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    @SuppressWarnings("unchecked")
    void setRouteErrors() throws Exception {
        var routeErrors = (List<String>) ReflectionTestUtils
                .getField(errorController, "routeErrors");
        assertNotNull(routeErrors);
        final var messageCountBefore =  routeErrors.size();

        errorController.addRouteErrors("Route error message");

        routeErrors = (List<String>) ReflectionTestUtils
                .getField(errorController, "routeErrors");
        assertNotNull(routeErrors);
        final var messageCountAfter = routeErrors.size();

        assertEquals(messageCountBefore + 1, messageCountAfter);
    }

    @Test
    @WithMockUser("ADMIN")
    void getRouteErrors() throws Exception {
        mockMvc.perform(get("/api/camel/routes/error")).andExpect(status().isOk()).andReturn();
    }
}
