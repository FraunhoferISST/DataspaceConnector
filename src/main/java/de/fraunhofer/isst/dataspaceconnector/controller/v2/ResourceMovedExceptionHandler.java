package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.ResourceMovedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class ResourceMovedExceptionHandler {
    @ExceptionHandler(ResourceMovedException.class)
    public ResponseEntity<Void> handleResourceNotFoundException(
            final ResourceMovedException exception) {
        var headers = new HttpHeaders();
        headers.setLocation(exception.getNewEndpoint().toUri());

        return new ResponseEntity<Void>(headers, HttpStatus.MOVED_PERMANENTLY);
    }
}
