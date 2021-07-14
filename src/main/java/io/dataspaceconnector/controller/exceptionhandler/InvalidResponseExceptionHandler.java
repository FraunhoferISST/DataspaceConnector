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

import io.dataspaceconnector.service.message.type.exceptions.InvalidResponse;
import io.dataspaceconnector.util.ControllerUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for {@link InvalidResponse}.
 */
@Log4j2
@ControllerAdvice
@Order
public final class InvalidResponseExceptionHandler {
    /**
     * Handle runtime exceptions with response code 500.
     *
     * @param exception The thrown exception.
     * @return Response entity with code 500.
     */
    @ExceptionHandler(InvalidResponse.class)
    public ResponseEntity<Object> handleAnyException(final InvalidResponse exception) {
        return ControllerUtils.respondWithMessageContent(exception.getContent());
    }
}
