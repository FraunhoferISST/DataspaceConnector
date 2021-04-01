package de.fraunhofer.isst.dataspaceconnector.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This class handles exceptions of type {@link JsonProcessingException}.
 */
@RestControllerAdvice
public class JsonProcessingExceptionHandler {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonProcessingExceptionHandler.class);

    /**
     * Handles thrown {@link JsonProcessingException}.
     *
     * @param exception The thrown exception.
     * @return A http response.
     */
    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(final JsonProcessingException exception) {
        LOGGER.warn("Invalid input. [exception=({})]", exception.getMessage());
        return new ResponseEntity<>("Invalid input.", HttpStatus.BAD_REQUEST);
    }
}
