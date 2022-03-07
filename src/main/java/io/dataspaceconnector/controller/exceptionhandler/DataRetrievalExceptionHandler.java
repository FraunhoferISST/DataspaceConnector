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

import io.dataspaceconnector.common.exception.DataRetrievalException;
import io.dataspaceconnector.common.net.JsonResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controller for handling {@link DataRetrievalException}.
 */
@ControllerAdvice
@Log4j2
@Order(1)
public final class DataRetrievalExceptionHandler {

    /**
     * Handles thrown {@link DataRetrievalException}.
     *
     * @param e The thrown exception.
     * @return Response entity with code 417.
     */
    @ExceptionHandler(DataRetrievalException.class)
    public ResponseEntity<Object> handleException(final DataRetrievalException e) {
        final var msg = "Failed to retrieve data.";
        if (log.isDebugEnabled()) {
            log.debug(msg + " [exception=({})]", e == null ? "" : e.getMessage(), e);
        }

        return new JsonResponse(msg, e == null ? "" : e.getMessage())
                .create(HttpStatus.EXPECTATION_FAILED);
    }
}
