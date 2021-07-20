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
package io.dataspaceconnector.controller.resource.swagger.response;

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
     * Response code is UNAUTHORIZED.
     */
    public static final String UNAUTHORIZED = "401";

    /**
     * Response code is METHOD_NOT_ALLOWED.
     */
    public static final String METHOD_NOT_ALLOWED = "405";
}
