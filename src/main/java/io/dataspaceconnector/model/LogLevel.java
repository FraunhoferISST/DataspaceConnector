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
package io.dataspaceconnector.model;

/**
 * Enumeration for log level.
 */
public enum LogLevel {

    /**
     * Log level is debugging.
     */
    DEBUG_LEVEL_LOGGING("Debug Level Logging"),
    /**
     * Log level is minimal.
     */
    MINIMAL_LOGGING("Minimal Logging"),
    /**
     * No logging is used.
     */
    NO_LOGGING("No Logging");

    /**
     * The value of the enum.
     */
    private final String value;

    /**
     * @param value The value of the enum.
     */
    LogLevel(String value) {
        this.value = value;
    }

    /**
     * @return Enum value.
     */
    @Override
    public String toString() {
        return value;
    }
}
