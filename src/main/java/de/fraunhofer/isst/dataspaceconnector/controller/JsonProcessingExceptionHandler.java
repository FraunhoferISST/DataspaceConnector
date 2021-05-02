package de.fraunhofer.isst.dataspaceconnector.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This class handles exceptions of type {@link JsonProcessingException}.
 */
@RestControllerAdvice
@Log4j2
public class JsonProcessingExceptionHandler {

    /**
     * Handles thrown {@link JsonProcessingException}.
     *
     * @param exception The thrown exception.
     * @return A http response.
     */
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(
            final JsonProcessingException exception) {
        if (log.isWarnEnabled()) {
            log.warn("Invalid input. [exception=({})]", exception == null ? ""
                    : exception.getMessage());
        }
        return ResponseEntity.badRequest().body("Invalid input.");
    }
}
