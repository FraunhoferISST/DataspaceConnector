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
package io.dataspaceconnector.common.ids.policy;

/**
 * Enum describing policy patterns supported by this ids connector.
 */
public enum PolicyPattern {

    /**
     * Standard pattern to allow unrestricted access.
     */
    PROVIDE_ACCESS,

    /**
     * Default pattern if no other is detected.
     */
    PROHIBIT_ACCESS,

    /**
     * Use the data not more than N times.
     */
    N_TIMES_USAGE,

    /**
     * Restrict the data usage to a specific time duration.
     */
    DURATION_USAGE,

    /**
     * Restrict the data usage to a specific time interval.
     */
    USAGE_DURING_INTERVAL,

    /**
     * Use data and delete it at a specific date time.
     */
    USAGE_UNTIL_DELETION,

    /**
     * Log the data usage information.
     */
    USAGE_LOGGING,

    /**
     * Notify a party or a specific group of users when the data is used.
     */
    USAGE_NOTIFICATION,

    /**
     * Restrict the data usage to specific connectors.
     */
    CONNECTOR_RESTRICTED_USAGE,

    /**
     * Restrict the data usage to specific security profile.
     */
    SECURITY_PROFILE_RESTRICTED_USAGE
}
