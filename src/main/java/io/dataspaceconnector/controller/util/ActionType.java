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

/**
 * Enum class for action types.
 */
public enum ActionType {

    /**
     * Start an app.
     */
    START("Start"),

    /**
     * Stop an app.
     */
    STOP("Stop"),

    /**
     * Delete an app.
     */
    DELETE("Delete"),

    /**
     * Describes an app.
     */
    DESCRIBE("Describe");

    /**
     * Holds the enums string.
     */
    private final String value;

    /**
     * Constructor.
     *
     * @param name The name of the action type.
     */
    ActionType(final String name) {
        this.value = name;
    }

    @Override
    public String toString() {
        return value;
    }

}
