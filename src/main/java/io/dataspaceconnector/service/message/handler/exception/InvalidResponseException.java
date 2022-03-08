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
package io.dataspaceconnector.service.message.handler.exception;

import lombok.Getter;

import java.util.Map;

/**
 * Thrown to indicate that an invalid response has been received.
 */
public class InvalidResponseException extends RuntimeException {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The multipart response as map.
     */
    @Getter
    private final Map<String, Object> response;

    /**
     * Constructs an InvalidResponseException with the specified response map and detail message.
     *
     * @param responseMap the response map.
     * @param msg the detail message.
     */
    public InvalidResponseException(final Map<String, Object> responseMap, final String msg) {
        super(msg);
        this.response = responseMap;
    }

}
