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

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dataspaceconnector.common.exception.InvalidEntityException;
import io.dataspaceconnector.common.net.JsonResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * This class handles exception of type {@link JsonProcessingException}.
 */
@RestControllerAdvice
@Log4j2
@Order(1)
public class JsonProcessingExceptionHandler {

    /**
     * Handles thrown {@link JsonProcessingException}.
     *
     * @param exception The thrown exception.
     * @return A http response.
     */
    @ExceptionHandler({JsonProcessingException.class, InvalidEntityException.class})
    public ResponseEntity<Object> handleException(final Exception exception) {
        final var msg = "Invalid input.";
        if (log.isWarnEnabled()) {
            log.warn(msg + " [exception=({})]", exception == null ? "" : exception.getMessage());
        }
        return new JsonResponse(msg).create(HttpStatus.BAD_REQUEST);
    }
}
