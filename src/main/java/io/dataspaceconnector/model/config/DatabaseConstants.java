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
package io.dataspaceconnector.model.config;

/**
 * This class contains static constants for configuring database columns.
 */
public final class DatabaseConstants {

    /**
     * The maximum length of database columns containing URIs.
     */
    public static final int URI_COLUMN_LENGTH = 2048;

    /**
     * The maximum length of the database columns for auth keys.
     */
    public static final int AUTH_KEY_LENGTH = 2048;

    /**
     * The maximum length of the database columns for auth values.
     */
    public static final int AUTH_VALUE_LENGTH = 2048;

    /**
     * The maximum length of the database columns for usernames.
     */
    public static final int AUTH_USERNAME_LENGTH = 2048;

    /**
     * The maximum length of the database columns for password.
     */
    public static final int AUTH_PASSWORD_LENGTH = 2048;

    /**
     * The maximum length of database columns containing descriptions.
     */
    public static final int DESCRIPTION_COLUMN_LENGTH = 4096;

    /**
     * The maximum length of the database column for endpoint location.
     */
    public static final int ENDPOINT_LOCATION_LENGTH = 2048;

    /**
     * Private constructor.
     */
    private DatabaseConstants() { }

}
