package de.fraunhofer.isst.dataspaceconnector.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class JsonMappingExceptionHandler {

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<String> handleJsonMappingException() {
        return new ResponseEntity<>("Invalid input.", HttpStatus.BAD_REQUEST);
    }
}
