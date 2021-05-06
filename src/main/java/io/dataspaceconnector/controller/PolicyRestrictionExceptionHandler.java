package io.dataspaceconnector.controller;

import io.dataspaceconnector.exceptions.PolicyRestrictionException;
import io.dataspaceconnector.exceptions.ResourceNotFoundException;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controller for handling {@link ResourceNotFoundException}.
 */
@ControllerAdvice
@Log4j2
@Order(1)
public final class PolicyRestrictionExceptionHandler {
    /**
     * Handle {@link PolicyRestrictionException}.
     *
     * @param exception The thrown exception.
     * @return Response entity with code 404.
     */
    @ExceptionHandler(PolicyRestrictionException.class)
    public ResponseEntity<JSONObject> handlePolicyRestrictionException(
            final PolicyRestrictionException exception) {
        if (log.isDebugEnabled()) {
            log.debug("Policy restriction detected. [exception=({})]", exception == null
                    ? "" : exception.getMessage(), exception);
        }

        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final var body = new JSONObject();
        body.put("message", "A policy restriction has been detected.");

        return new ResponseEntity<>(body, headers, HttpStatus.FORBIDDEN);
    }
}
