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
package io.dataspaceconnector.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides an endpoint for handling errors on http requests.
 */
@Log4j2
@RestController
public class ErrorPageController implements ErrorController {

    /**
     * Method providing an error page for status codes indicating an error.
     *
     * @param request The http request.
     * @return A custom message and the status code as response object.
     */
    @Hidden
    @RequestMapping("/error")
    public ResponseEntity<Object> handleError(final HttpServletRequest request) {
        final var status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (log.isDebugEnabled()) {
            log.debug("Page could not be accessed. [status=({})]", status);
        }

        if (status != null) {
            final var statusCode = Integer.parseInt(status.toString());
            return new ResponseEntity<>(String.format("Error with status code %s.", statusCode),
                    HttpStatus.valueOf(statusCode));
        }

        return new ResponseEntity<>("Something went wrong.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
