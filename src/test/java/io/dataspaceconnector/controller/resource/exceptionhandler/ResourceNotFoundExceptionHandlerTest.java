package io.dataspaceconnector.controller.resource.exceptionhandler;

import io.dataspaceconnector.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResourceNotFoundExceptionHandlerTest {

    private ResourceNotFoundExceptionHandler exceptionHandler =
            new ResourceNotFoundExceptionHandler();

    @Test
    public void handleResourceNotFoundException_returnHttpStatusNotFound() {
        /* ACT */
        final var response = exceptionHandler
                .handleResourceNotFoundException(new ResourceNotFoundException("Resource not found."));

        /* ASSERT */
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        final var headers = response.getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());

        final var body = response.getBody();
        assertEquals("Resource not found.", body.get("message"));
    }

}
