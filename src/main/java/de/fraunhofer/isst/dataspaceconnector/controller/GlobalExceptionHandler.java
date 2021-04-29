package de.fraunhofer.isst.dataspaceconnector.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;

/**
 * Controller for global handling of runtime exceptions.
 */
@Log4j2
@ControllerAdvice
public final class GlobalExceptionHandler {
    /**
     * Handle runtime exceptions with response code 500.
     *
     * @param exception The thrown exception.
     * @return Response entity with code 500.
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<JSONObject> handleAnyException(final RuntimeException exception) {
        if (log.isErrorEnabled()) {
            log.error("An unhandled exception has been caught. [exception=({})]",
                    exception != null ? exception.getMessage() : "Passed null as exception",
                    exception);
        }

        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Error", "true");

        final var body = new JSONObject();
        body.put("message", "An error occurred. Please try again later.");

        return new ResponseEntity<>(body, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
