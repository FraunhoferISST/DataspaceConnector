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
package io.dataspaceconnector.common.ids.policy;

/**
 * Enum describing policy patterns supported by this ids connector.
 */
public enum PolicyPattern {

    /**
     * Standard pattern to allow unrestricted access.
     */
    PROVIDE_ACCESS("Provide unrestricted data access"),

    /**
     * Default pattern if no other is detected.
     */
    PROHIBIT_ACCESS("Prohibit the data access"),

    /**
     * Use the data not more than N times.
     */
    N_TIMES_USAGE("Restrict the data usage to not more than N times"),

    /**
     * Restrict the data usage to a specific time duration.
     */
    DURATION_USAGE("Restrict the data usage to a specific time duration"),

    /**
     * Restrict the data usage to a specific time interval.
     */
    USAGE_DURING_INTERVAL("Restrict the data usage to a specific time interval"),

    /**
     * Use data and delete it at a specific date time.
     */
    USAGE_UNTIL_DELETION("Use data and delete it at a specific date time"),

    /**
     * Log the data usage information.
     */
    USAGE_LOGGING("Log the data usage information"),

    /**
     * Notify a party or a specific group of users when the data is used.
     */
    USAGE_NOTIFICATION("Notify a party or a specific group of users when the data is used"),

    /**
     * Restrict the data usage to specific connectors.
     */
    CONNECTOR_RESTRICTED_USAGE("Restrict the data usage to a specific connector"),

    /**
     * Restrict the data usage to specific security profile.
     */
    SECURITY_PROFILE_RESTRICTED_USAGE("Restrict the data usage to a security profile");

    /**
     * Holds the enums string.
     */
    private final String value;

    /**
     * Constructor.
     *
     * @param name The name of the policy pattern.
     */
    PolicyPattern(final String name) {
        this.value = name;
    }

    @Override
    public String toString() {
        return value;
    }

}
