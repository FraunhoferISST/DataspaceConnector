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

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    public void handleAnyException_anyException_returnInternalServerError() {
        /* ARRANGE */
        final var exception = new RuntimeException("Some problem");

        /* ACT */
        final var result = handler.handleException(exception);

        /* ASSERT */
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void handleAnyException_anyException_returnXErrorHeader() {
        /* ARRANGE */
        final var exception = new RuntimeException("Some problem");

        /* ACT */
        final var result = handler.handleException(exception);

        /* ASSERT */
        assertTrue(result.getHeaders().containsKey("X-Error"));
        assertEquals("true", result.getHeaders().get("X-Error").get(0));
    }

    @Test
    public void handleAnyException_anyException_returnJsonContentType() {
        /* ARRANGE */
        final var exception = new RuntimeException("Some problem");

        /* ACT */
        final var result = handler.handleException(exception);

        /* ASSERT */
        assertEquals(MediaType.APPLICATION_JSON, result.getHeaders().getContentType());
    }

    @Test
    public void handleAnyException_anyException_returnJsonObject() {
        /* ARRANGE */
        final var body = new JSONObject();
        body.put("message", "An error occurred. Please try again later.");

        final var exception = new RuntimeException("Some problem");

        /* ACT */
        final var result = handler.handleException(exception);

        /* ASSERT */
        assertEquals(body, result.getBody());
    }

    @Test
    public void handleAnyException_null_returnJsonObject() {
        /* ARRANGE */
        final var body = new JSONObject();
        body.put("message", "An error occurred. Please try again later.");

        /* ACT */
        final var result = handler.handleException(null);

        /* ASSERT */
        assertEquals(body, result.getBody());
    }
}
