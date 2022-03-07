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
 * This class holds information about response codes.
 */
public class ResponseCode {

    protected ResponseCode() {
        throw new UnsupportedOperationException();
    }

    /**
     * Response code is OK.
     */
    @SuppressWarnings("PMD.ShortVariable")
    public static final String OK = "200";

    /**
     * Response code is CREATED.
     */
    public static final String CREATED = "201";

    /**
     * Response code is NO_CONTENT.
     */
    public static final String NO_CONTENT = "204";

    /**
     * Response code is NOT_MODIFIED.
     */
    public static final String NOT_MODIFIED = "304";

    /**
     * Response code is BAD_REQUEST.
     */
    public static final String BAD_REQUEST = "400";

    /**
     * Response code is UNAUTHORIZED.
     */
    public static final String UNAUTHORIZED = "401";

    /**
     * Response code is NOT_FOUND.
     */
    public static final String NOT_FOUND = "404";

    /**
     * Response code is METHOD_NOT_ALLOWED.
     */
    public static final String METHOD_NOT_ALLOWED = "405";

    /**
     * Response code is CONFLICT.
     */
    public static final String CONFLICT = "409";

    /**
     * Response code is UNSUPPORTED_MEDIA_TYPE.
     */
    public static final String UNSUPPORTED_MEDIA_TYPE = "415";

    /**
     * Response code is INTERNAL_SERVER_ERROR.
     */
    public static final String INTERNAL_SERVER_ERROR = "500";

    /**
     * Response code is BAD_GATEWAY.
     */
    public static final String BAD_GATEWAY = "502";
}
