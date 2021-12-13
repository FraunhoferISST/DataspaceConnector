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
package io.dataspaceconnector.controller.util;

import io.dataspaceconnector.common.exception.ErrorMessage;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ResponseUtils.class})
class ResponseUtilsTest {

    private final Exception exception = new Exception("Some exception.");

    @Test
    public void respondIdsMessageFailed_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var msg = ErrorMessage.MESSAGE_HANDLING_FAILED.toString();
        final var expectedResponse = new ResponseEntity<>(new JSONObject().put("message", msg),
                HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ResponseUtils.respondIdsMessageFailed(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondReceivedInvalidResponse_validException_returnValidResponseEntity() {
        /* ARRANGE */

        /* ACT */
        final var response = ResponseUtils.respondReceivedInvalidResponse(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(502, response.getStatusCodeValue());
    }

    @Test
    public void respondConfigurationUpdateError_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var msg = "Failed to update configuration.";
        final var expectedResponse = new ResponseEntity<>(new JSONObject().put("message", msg),
                HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ResponseUtils.respondConfigurationUpdateError(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondDeserializationError_validUri_returnValidResponseEntity() {
        /* ARRANGE */
        final var msg = "Resource not found";
        final var resourceId = URI.create("https://requestedResource");
        final var expectedResponse = new ResponseEntity<>(new JSONObject().put("message", msg),
                HttpStatus.NOT_FOUND);

        /* ACT */
        final var response = ResponseUtils.respondResourceNotFound(resourceId);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondPatternNotIdentified_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var msg = "Could not identify pattern.";
        final var expectedResponse = new ResponseEntity<>(new JSONObject().put("message", msg),
                HttpStatus.BAD_REQUEST);

        /* ACT */
        final var response = ResponseUtils.respondPatternNotIdentified(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondInvalidInput_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var msg = "Invalid input, processing failed.";
        final var expectedResponse = new ResponseEntity<>(new JSONObject() {{
            put("message", msg);
            put("details", exception.getMessage());
        }}, HttpStatus.BAD_REQUEST);

        /* ACT */
        final var response = ResponseUtils.respondInvalidInput(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
    }

    @Test
    public void respondFailedToBuildContractRequest_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var msg = "Failed to build contract request.";
        final var expectedResponse = new ResponseEntity<>(new JSONObject().put("message", msg),
                HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ResponseUtils.respondFailedToBuildContractRequest(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondFailedToStoreEntity_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var msg = "Failed to store entity.";
        final var expectedResponse = new ResponseEntity<>(new JSONObject() {{
            put("message", msg);
            put("details", exception.getMessage());
        }}, HttpStatus.INTERNAL_SERVER_ERROR);

        /* ACT */
        final var response = ResponseUtils.respondFailedToStoreEntity(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondConnectionTimedOut_validException_returnValidResponseEntity() {
        /* ARRANGE */
        final var msg = ErrorMessage.GATEWAY_TIMEOUT.toString();
        final var expectedResponse = new ResponseEntity<>(new JSONObject().put("message", msg),
                HttpStatus.GATEWAY_TIMEOUT);

        /* ACT */
        final var response = ResponseUtils.respondConnectionTimedOut(exception);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(expectedResponse, response);
    }

    @Test
    public void respondReceivedInvalidResponse_null_returnValidResponseEntity() {
        /* ARRANGE */

        /* ACT */
        final var response = ResponseUtils.respondReceivedInvalidResponse();

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
        assertEquals(502, response.getStatusCodeValue());
    }

    @Test
    public void respondWithMessageContent_validMap_returnValidResponseEntity() {
        /* ARRANGE */
        final var obj = new Object();
        final var map = Map.of("header", obj);

        /* ACT */
        final var response = ResponseUtils.respondWithContent(map);

        /* ARRANGE */
        assertEquals(ResponseEntity.class, response.getClass());
    }
}
