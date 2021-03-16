package de.fraunhofer.isst.dataspaceconnector.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This class handles exceptions of type {@link JsonMappingException}.
 */
@RestControllerAdvice
public class JsonMappingExceptionHandler {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonMappingExceptionHandler.class);

    /**
     * Handles thrown {@link JsonMappingException}.
     *
     * @param exception The thrown exception.
     * @return A http response.
     */
    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<String> handleJsonMappingException(final JsonMappingException exception) {
        LOGGER.warn("Invalid input. [exception=({})]", exception.getMessage());
        return new ResponseEntity<>("Invalid input.", HttpStatus.BAD_REQUEST);
    }
}
