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
package io.dataspaceconnector.controller.util;

/**
 * This class holds descriptions for the responses.
 */
public class ResponseDescription {
    protected ResponseDescription() {
        throw new UnsupportedOperationException();
    }

    /**
     * Response code is OK.
     */
    @SuppressWarnings("PMD.ShortVariable")
    public static final String OK = "Ok";

    /**
     * Response code is CREATED.
     */
    public static final String CREATED = "Created";

    /**
     * Response code is NO_CONTENT.
     */
    public static final String NO_CONTENT = "No content";

    /**
     * Response code is UNAUTHORIZED.
     */
    public static final String UNAUTHORIZED = "Unauthorized";

    /**
     * Response code is BAD_REQUEST.
     */
    public static final String BAD_REQUEST = "Bad request";

    /**
     * Response code is INTERNAL_SERVER_ERROR.
     */
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";

    /**
     * Response code is METHOD_NOT_ALLOWED.
     */
    public static final String METHOD_NOT_ALLOWED = "Not allowed";

    /**
     * Response code is UNSUPPORTED_MEDIA_TYPE.
     */
    public static final String UNSUPPORTED_MEDIA_TYPE = "Unsupported media type";

    /**
     * Response code is NOT_MODIFIED.
     */
    public static final String NOT_MODIFIED = "Not modified";

    /**
     * Response code is CONFLICT.
     */
    public static final String CONFLICT = "Conflict";

    /**
     * Response code is NOT_FOUND.
     */
    public static final String NOT_FOUND = "Not found";

    /**
     * Response code is BAD_GATEWAY.
     */
    public static final String BAD_GATEWAY = "Bad Gateway";
}
