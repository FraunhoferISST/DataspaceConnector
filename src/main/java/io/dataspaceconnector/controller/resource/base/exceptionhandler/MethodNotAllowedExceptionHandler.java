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

import io.dataspaceconnector.controller.resource.base.exception.MethodNotAllowed;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controller for handling {@link MethodNotAllowed}.
 */
@ControllerAdvice
@Order(1)
public class MethodNotAllowedExceptionHandler {
    /**
     * Handles thrown {@link MethodNotAllowed}.
     *
     * @return Response entity with code 405.
     */
    @ExceptionHandler(MethodNotAllowed.class)
    public ResponseEntity<Void> handlePolicyRestrictionException() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
