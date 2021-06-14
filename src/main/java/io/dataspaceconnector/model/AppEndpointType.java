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
 * Enum describing app endpoint types.
 */
public enum AppEndpointType {

    /**
     * Endpoint is used for runtime-related app configurations and app parameters.
     */
    CONFIG_ENDPOINT("Configuration Endpoint"),
    /**
     * The default endpoint.
     */
    DEFAULT_ENDPOINT("Default Endpoint"),
    /**
     * Endpoint is used for data input.
     */
    INPUT_ENDPOINT("Input Endpoint"),
    /**
     * Endpoint is used for data output.
     */
    OUTPUT_ENDPOINT("Output Endpoint"),
    /**
     * Endpoint is used to start (or control) app processing.
     */
    PROCESS_ENDPOINT("Process Endpoint"),
    /**
     * Pre-defined endpoint used to return the corresponding self-description of the data app.
     */
    SELF_DESCRIPTION_ENDPOINT("Self description Endpoint"),
    /**
     * Endpoint is used to get app lifecycle status.
     */
    STATUS_ENDPOINT("Status Endpoint"),
    /**
     * Endpoint is used for usage control related scenarios.
     */
    USAGE_POLICY_ENDPOINT("Usage Policy Endpoint");

    /**
     * The value of the enum.
     */
    private final String value;

    /**
     * @param enumValue The value of the enum.
     */
    AppEndpointType(final String enumValue) {
        this.value = enumValue;
    }

    /**
     * @return The value.
     */
    @Override
    public String toString() {
        return value;
    }

}
