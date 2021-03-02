package de.fraunhofer.isst.dataspaceconnector.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
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
     * Handles thrown {@link JsonMappingException}.
     *
     * @return A http response.
     */
    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<String> handleJsonMappingException() {
        return new ResponseEntity<>("Invalid input.", HttpStatus.BAD_REQUEST);
    }
}
