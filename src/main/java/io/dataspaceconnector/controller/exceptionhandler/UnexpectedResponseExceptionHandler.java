/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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

import io.dataspaceconnector.common.exception.UnexpectedResponseException;
import io.dataspaceconnector.controller.util.ResponseUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for {@link UnexpectedResponseException}.
 */
@ControllerAdvice
@Order(1)
public final class UnexpectedResponseExceptionHandler {

    /**
     * Handles thrown runtime exception with response code 417.
     *
     * @param exception The thrown exception.
     * @return Response entity with code 417.
     */
    @ExceptionHandler(UnexpectedResponseException.class)
    public ResponseEntity<Object> handleAnyException(final UnexpectedResponseException exception) {
        return ResponseUtils.respondWithContent(exception.getContent());
    }
}
