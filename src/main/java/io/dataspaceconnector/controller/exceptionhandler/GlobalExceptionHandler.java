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
package io.dataspaceconnector.controller.exceptionhandler;

import io.dataspaceconnector.common.net.JsonResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controller for global handling of runtime exception.
 */
@Log4j2
@ControllerAdvice
@Order
public final class GlobalExceptionHandler {

    /**
     * Handles thrown runtime exception with response code 500.
     *
     * @param exception The thrown exception.
     * @return Response entity with code 500.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleException(final RuntimeException exception) {
        if (log.isErrorEnabled()) {
            log.error("An unhandled exception has been caught. [exception=({})]",
                    exception == null ? "Passed null as exception" : exception.getMessage(),
                    exception);
        }

        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Error", "true");

        return new JsonResponse("An error occurred. Please try again later.")
                .create(headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
