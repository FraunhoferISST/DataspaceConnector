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
package io.dataspaceconnector.controller.gui.util;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Provides enum types that should be listed at the GUI.
 */
public enum EnumType {

    /**
     * The log level.
     */
    @JsonProperty("loglevel")
    LOG_LEVEL,

    /**
     * The connector status.
     */
    @JsonProperty("connectorstatus")
    CONNECTOR_STATUS,

    /**
     * The connector deploy mode.
     */
    @JsonProperty("connectordeploymode")
    CONNECTOR_DEPLOY_MODE,

    /**
     * The language.
     */
    @JsonProperty("language")
    LANGUAGE,

    /**
     * The deploy method of data routes.
     */
    @JsonProperty("deploymethod")
    DEPLOY_METHOD,

    /**
     * The broker status.
     */
    @JsonProperty("brokerstatus")
    BROKER_STATUS,

    /**
     * The security profile.
     */
    @JsonProperty("securityprofile")
    SECURITY_PROFILE,

    /**
     * The payment method.
     */
    @JsonProperty("paymentmethod")
    PAYMENT_METHOD,
}
