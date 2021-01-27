package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceMovedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class ResourceMovedExceptionHandler {
    @ExceptionHandler(ResourceMovedException.class)
    public ResponseEntity<Void> handleResourceNotFoundException(
            final ResourceMovedException exception) {
        return ResponseEntity.notFound().build();
    }
}
