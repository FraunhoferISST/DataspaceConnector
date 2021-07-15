package io.configmanager.extensions.routes.camel;

import io.configmanager.extensions.routes.camel.exceptions.NoSuitableTemplateException;
import io.configmanager.extensions.routes.camel.exceptions.RouteCreationException;
import io.configmanager.extensions.routes.camel.exceptions.RouteDeletionException;
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
