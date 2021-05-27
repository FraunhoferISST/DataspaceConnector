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
 * Enumeration for endpoint type.
 */
public enum EndpointType {

    /**
     * The generic endpoint.
     */
    GENERIC_ENDPOINT("Generic endpoint"),
    /**
     * The app endpoint.
     */
    APP_ENDPOINT("App endpoint"),
    /**
     * The ids endpoint.
     */
    IDS_ENDPOINT("IDS endpoint");

    /**
     * The value of the enum.
     */
    private final String value;

    /**
     * @param value The value of the enum.
     */
    EndpointType(String value) {
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
