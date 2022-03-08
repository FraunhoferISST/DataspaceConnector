/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.controller.resource.base.exceptionhandler;

import io.dataspaceconnector.common.exception.RouteDeletionException;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This class handles exception of type {@link RouteDeletionException}.
 */
@RestControllerAdvice
@Log4j2
@Order(1)
public class RouteDeletionExceptionHandler {

    /**
     * Handles thrown {@link RouteDeletionException}.
     *
     * @param exception the thrown exception.
     * @return an HTTP response.
     */
    @ExceptionHandler(RouteDeletionException.class)
    public ResponseEntity<JSONObject> handleRouteDeletionException(
            final RouteDeletionException exception) {
        if (log.isWarnEnabled()) {
            if (exception != null) {
                log.warn("Failed to delete Camel route. [exception=({})] [cause=({})]",
                        exception.getMessage(),
                        exception.getCause() == null ? "" : exception.getCause().getMessage());
            } else {
                log.warn("Failed to delete Camel route.");
            }
        }

        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final var body = new JSONObject();
        body.put("message", "Failed to delete Camel route.");

        return new ResponseEntity<>(body, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
