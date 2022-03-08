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
package io.dataspaceconnector.common.exception;

import lombok.Getter;

import java.util.Map;

/**
 * Thrown when a message response is invalid.
 */
@Getter
public class UnexpectedResponseException extends Exception {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The content of the invalid response.
     */
    private final Map<String, Object> content;

    /**
     * Create a new invalid response.
     * @param responseContent The content of the invalid response.
     */
    public UnexpectedResponseException(final Map<String, Object> responseContent) {
        super();
        this.content = responseContent;
    }

    /**
     * Create a new invalid response.
     * @param response The content of the invalid response.
     * @param cause The exception to wrap.
     */
    public UnexpectedResponseException(final Map<String, Object> response, final Throwable cause) {
        super(cause);
        this.content = response;
    }
}
