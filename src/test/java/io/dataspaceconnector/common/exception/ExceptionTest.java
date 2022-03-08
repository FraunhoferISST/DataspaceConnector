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
package io.dataspaceconnector.common.exception;

import io.dataspaceconnector.service.routing.exception.NoSuitableTemplateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExceptionTest {
    @Test
    void testNoSuitableTemplateException() {
        final var exception = new NoSuitableTemplateException("test");
        assertEquals("test", exception.getMessage());
        assertEquals(exception.getClass(), NoSuitableTemplateException.class);
    }

    @Test
    void testRouteCreationException() {
        final var exception = new RouteCreationException("test");
        assertEquals("test", exception.getMessage());
        assertEquals(exception.getClass(), RouteCreationException.class);

        final var exception2 = new RouteCreationException("test", null);
        assertEquals("test", exception2.getMessage());
        assertEquals(exception2.getClass(), RouteCreationException.class);
    }

    @Test
    void testRouteDeletionException() {
        final var exception = new RouteDeletionException("test", null);
        assertEquals("test", exception.getMessage());
        assertEquals(exception.getClass(), RouteDeletionException.class);
    }
}
