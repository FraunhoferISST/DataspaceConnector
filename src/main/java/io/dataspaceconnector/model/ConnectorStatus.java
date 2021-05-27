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
 * Enumeration for connector status.
 */
public enum ConnectorStatus {

    /**
     * Connector is badly configured.
     */
    CONNECTOR_BADLY_CONFIGURED("Connector badly configured"),
    /**
     * Connector is offline.
     */
    CONNECTOR_OFFLINE("Connector offline"),
    /**
     * Connector is online.
     */
    CONNECTOR_ONLINE("Connector online");

    /**
     * The value of the enum.
     */
    private final String value;

    /**
     * @param value The value of the enum.
     */
    ConnectorStatus(String value) {
        this.value = value;
    }

    /**
     * @return The value.
     */
    @Override
    public String toString() {
        return value;
    }
}
