package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controller for handling {@link ResourceNotFoundException}.
 */
@ControllerAdvice
@Log4j2
public final class ResourceNotFoundExceptionHandler {
    /**
     * Handle {@link ResourceNotFoundException}.
     *
     * @param exception The thrown exception.
     * @return Response entity with code 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Void> handleResourceNotFoundException(
            final ResourceNotFoundException exception) {
        if (log.isDebugEnabled()) {
            log.debug("Resource not found. [exception=({})]", exception == null ? ""
                    : exception.getMessage(), exception);
        }
        return ResponseEntity.notFound().build();
    }
}
