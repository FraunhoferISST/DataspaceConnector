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
package io.dataspaceconnector.extension.monitoring.update.util;

/**
 * Types which updates may be present.
 */
public enum UpdateType {

    /**
     * If no update is available.
     */
    NO_UPDATE("None"),

    /**
     * A new major release is available.
     */
    MAJOR("Major"),

    /**
     * A new minor release is available.
     */
    MINOR("Minor"),

    /**
     * A new patch release is available.
     */
    PATCH("Patch");

    /**
     * Holds the enums string.
     */
    private final String value;

    /**
     * Constructor.
     *
     * @param name The name of the update type.
     */
    UpdateType(final String name) {
        this.value = name;
    }

    @Override
    public String toString() {
        return value;
    }
}
