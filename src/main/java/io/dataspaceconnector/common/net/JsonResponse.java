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
package io.dataspaceconnector.common.net;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Helper class for building an http response entity.
 */
public class JsonResponse {

    /**
     * The built json response body.
     */
    private final JSONObject body;

    /**
     * Constructor with all three parameters.
     *
     * @param message The message.
     * @param details Detailed information, e.g. a specific error.
     * @param value   A value that should be included.
     */
    public JsonResponse(final Object message, final Object details, final Object value) {
        final var obj = new JSONObject();
        if (message != null) {
            obj.put("message", message);
        }

        if (details != null) {
            obj.put("details", details);
        }

        if (value != null) {
            obj.put("value", value);
        }

        this.body = obj;
    }

    /**
     * Constructor with two parameters.
     *
     * @param message The message.
     * @param details Detailed information, e.g. a specific error.
     */
    public JsonResponse(final Object message, final Object details) {
        final var obj = new JSONObject();
        if (message != null) {
            obj.put("message", message);
        }

        if (details != null) {
            obj.put("details", details);
        }

        this.body = obj;
    }

    /**
     * Constructor with message parameter.
     *
     * @param message The message.
     */
    public JsonResponse(final Object message) {
        final var obj = new JSONObject();
        if (message != null) {
            obj.put("message", message);
        }

        this.body = obj;
    }

    /**
     * Create response from json object with http headers and status.
     *
     * @param headers The custom http headers.
     * @param status  The http status.
     * @return the built response entity.
     */
    public ResponseEntity<Object> create(final HttpHeaders headers, final HttpStatus status) {
        return new ResponseEntity<>(body, headers, status);
    }

    /**
     * Create response from json object with http status.
     *
     * @param status The http status.
     * @return the built response entity.
     */
    public ResponseEntity<Object> create(final HttpStatus status) {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(body, headers, status);
    }
}
