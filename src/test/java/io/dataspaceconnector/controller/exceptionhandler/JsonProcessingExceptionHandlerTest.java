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
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonProcessingExceptionHandlerTest {

    private JsonProcessingExceptionHandler handler = new JsonProcessingExceptionHandler();


    @Test
    public void handleJsonProcessingException_anyException_returnBadRequest() {
        /* ARRANGE */
        final var exception = new JsonProcessingException("Some problem"){};

        /* ACT */
        final var result = handler.handleException(exception);

        /* ASSERT */
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void handleJsonProcessingException_anyException_returnJsonContentType() {
        /* ARRANGE */
        final var exception = new JsonProcessingException("Some problem"){};

        /* ACT */
        final var result = handler.handleException(exception);

        /* ASSERT */
        assertEquals(MediaType.APPLICATION_JSON, result.getHeaders().getContentType());
    }

    @Test
    public void handleJsonProcessingException_anyException_returnJsonObject() {
        /* ARRANGE */
        final var body = new JSONObject();
        body.put("message", "Invalid input.");

        final var exception = new JsonProcessingException("Some problem"){};

        /* ACT */
        final var result = handler.handleException(exception);

        /* ASSERT */
        assertEquals(body, result.getBody());
    }

    @Test
    public void handleJsonProcessingException_null_returnJsonObject() {
        /* ARRANGE */
        final var body = new JSONObject();
        body.put("message", "Invalid input.");

        /* ACT */
        final var result = handler.handleException(null);

        /* ASSERT */
        assertEquals(body, result.getBody());
    }
}
