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
package io.dataspaceconnector.controller.gui.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iais.eis.Language;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.ids.policy.PolicyPattern;
import io.dataspaceconnector.common.ids.policy.UsageControlFramework;
import io.dataspaceconnector.controller.util.ActionType;
import io.dataspaceconnector.extension.monitoring.update.util.UpdateType;
import io.dataspaceconnector.model.base.RegistrationStatus;
import io.dataspaceconnector.model.configuration.ConnectorStatus;
import io.dataspaceconnector.model.configuration.DeployMethod;
import io.dataspaceconnector.model.configuration.DeployMode;
import io.dataspaceconnector.model.configuration.LogLevel;
import io.dataspaceconnector.model.configuration.SecurityProfile;
import io.dataspaceconnector.model.datasource.DataSourceType;
import io.dataspaceconnector.model.endpoint.EndpointType;
import io.dataspaceconnector.model.resource.PaymentMethod;
import io.dataspaceconnector.service.message.util.Event;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * The class can be used to define auxiliary methods that are needed again and again.
 */
@Log4j2
public final class GuiUtils {

    private GuiUtils() {
        // not used
    }

    /**
     * The method returns all enums and their values.
     *
     * @return enums as json object.
     */
    public static JSONObject getListOfEnums() {
        return new JSONObject() {{
            put("types", getTypes());
            put(EnumType.LOG_LEVEL.toString(), getLogLevel());
            put(EnumType.CONNECTOR_STATUS.toString(), getConnectorStatus());
            put(EnumType.IDS_CONNECTOR_STATUS.toString(), getIdsConnectorStatus());
            put(EnumType.CONNECTOR_DEPLOY_MODE.toString(), getConnectorDeployMode());
            put(EnumType.LANGUAGE.toString(), getLanguage());
            put(EnumType.DEPLOY_METHOD.toString(), getDeployMethod());
            put(EnumType.BROKER_STATUS.toString(), getBrokerStatus());
            put(EnumType.SECURITY_PROFILE.toString(), getSecurityProfile());
            put(EnumType.IDS_SECURITY_PROFILE.toString(), getIdsSecurityProfile());
            put(EnumType.PAYMENT_METHOD.toString(), getPaymentMethod());
            put(EnumType.POLICY_PATTERN.toString(), getPolicyPattern());
            put(EnumType.UPDATE_TYPE.toString(), getUpdateType());
            put(EnumType.ENDPOINT_TYPE.toString(), getEndpointType());
            put(EnumType.EVENT_TYPE.toString(), getEventType());
            put(EnumType.ERROR_MESSAGE.toString(), getErrorMessage());
            put(EnumType.USAGE_CONTROL_FRAMEWORK.toString(), getUsageControlFramework());
            put(EnumType.ACTION_TYPE.toString(), getActionType());
            put(EnumType.DATA_SOURCE_TYPE.toString(), getDataSourceType());
        }};
    }

    private static JSONArray getTypes() {
        final var jsonArray = new JSONArray();
        jsonArray.addAll(Arrays.asList(EnumType.values()));

        return jsonArray;
    }

    private static JSONArray getPaymentMethod() {
        final var jsonArray = new JSONArray();
        for (final var paymentMethod : PaymentMethod.values()) {
            try {
                jsonArray.add(new JSONObject() {{
                    put("originalName", paymentMethod.name());
                    put("displayName", paymentMethod.toString());
                    put("jsonInput", PaymentMethod.class.getField(paymentMethod.name())
                            .getAnnotation(JsonProperty.class).value());
                }});
            } catch (NoSuchFieldException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Missing JsonProperty found for paymentMethod.");
                }
            }

        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getSecurityProfile() {
        final var jsonArray = new JSONArray();
        for (final var securityProfile : SecurityProfile.values()) {
            try {
                jsonArray.add(new JSONObject() {{
                    put("originalName", securityProfile.name());
                    put("displayName", SecurityProfile.class.getField(securityProfile.name())
                            .getAnnotation(JsonProperty.class).value());
                }});
            } catch (NoSuchFieldException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Missing JsonProperty found for securityProfile.");
                }
            }
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getIdsSecurityProfile() {
        final var jsonArray = new JSONArray();
        for (final var securityProfile : de.fraunhofer.iais.eis.SecurityProfile.values()) {
            jsonArray.add(new JSONObject() {{
                put("originalName", securityProfile.name());
                put("displayName", securityProfile.getLabel().get(0).getValue());
                put("jsonInput", securityProfile.getId());
            }});
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getBrokerStatus() {
        final var jsonArray = new JSONArray();
        for (final var brokerStatus : RegistrationStatus.values()) {
            try {
                final var value = RegistrationStatus.class.getField(brokerStatus.name())
                        .getAnnotation(JsonProperty.class).value();
                jsonArray.add(new JSONObject() {{
                    put("originalName", brokerStatus.name());
                    put("displayName", value);
                }});
            } catch (NoSuchFieldException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Missing JsonProperty found for registrationStatus.");
                }
            }
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getDeployMethod() {
        final var jsonArray = new JSONArray();
        for (final var deployMethod : DeployMethod.values()) {
            try {
                final var value = DeployMethod.class.getField(deployMethod.name())
                        .getAnnotation(JsonProperty.class).value();
                jsonArray.add(new JSONObject() {{
                    put("originalName", deployMethod.name());
                    put("displayName", value);
                    put("jsonInput", value);
                }});
            } catch (NoSuchFieldException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Missing JsonProperty found for deployMethod.");
                }
            }
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getLanguage() {
        final var jsonArray = new JSONArray();
        for (final var language : Language.values()) {
            // Workaround for Infomodel issue LT = LessThan
            String value;
            if ("LT".equals(language.name())) {
                value = language.getLabel().get(1).getValue();
            } else {
                value = language.getLabel().get(0).getValue();
            }
            jsonArray.add(new JSONObject() {{
                put("originalName", language.name());
                put("displayName", value);
                put("jsonInput", language.name());
            }});
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getConnectorDeployMode() {
        final var jsonArray = new JSONArray();
        for (final var connectorDeployMode : DeployMode.values()) {
            try {
                final var value = DeployMode.class.getField(connectorDeployMode.name())
                        .getAnnotation(JsonProperty.class).value();
                jsonArray.add(new JSONObject() {{
                    put("originalName", connectorDeployMode.name());
                    put("displayName", value);
                }});
            } catch (NoSuchFieldException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Missing JsonProperty found for connectorDeployMode.");
                }
            }
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getConnectorStatus() {
        final var jsonArray = new JSONArray();
        for (final var connectorStatus : ConnectorStatus.values()) {
            try {
                final var value = ConnectorStatus.class.getField(connectorStatus.name())
                        .getAnnotation(JsonProperty.class).value();
                jsonArray.add(new JSONObject() {{
                    put("originalName", connectorStatus.name());
                    put("displayName", value);
                    put("jsonInput", value);
                }});
            } catch (NoSuchFieldException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Missing JsonProperty found for connectorStatus.");
                }
            }
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getIdsConnectorStatus() {
        final var jsonArray = new JSONArray();
        for (final var connectorStatus : de.fraunhofer.iais.eis.ConnectorStatus.values()) {
            jsonArray.add(new JSONObject() {{
                put("originalName", connectorStatus.name());
                put("displayName", connectorStatus.getLabel().get(0).getValue());
            }});
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getLogLevel() {
        final var jsonArray = new JSONArray();
        for (final var logLevel : LogLevel.values()) {
            try {
                final var value = LogLevel.class.getField(logLevel.name())
                        .getAnnotation(JsonProperty.class).value();
                jsonArray.add(new JSONObject() {{
                    put("originalName", logLevel.name());
                    put("displayName", value);
                    put("jsonInput", value);
                }});
            } catch (NoSuchFieldException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Missing JsonProperty found for logLevel.");
                }
            }
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getPolicyPattern() {
        final var jsonArray = new JSONArray();
        for (final var policyPattern : PolicyPattern.values()) {
            jsonArray.add(new JSONObject() {{
                put("originalName", policyPattern.name());
                put("displayName", policyPattern.toString());
                put("jsonInput", policyPattern.name());
            }});
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getUpdateType() {
        final var jsonArray = new JSONArray();
        for (final var updateType : UpdateType.values()) {
            jsonArray.add(new JSONObject() {{
                put("originalName", updateType.name());
                put("displayName", updateType.toString());
            }});
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getEndpointType() {
        final var jsonArray = new JSONArray();
        for (final var endpointType : EndpointType.values()) {
            jsonArray.add(new JSONObject() {{
                put("originalName", endpointType.name());
                put("displayName", endpointType.toString());
                put("jsonInput", endpointType.name());
            }});
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getEventType() {
        final var jsonArray = new JSONArray();
        for (final var eventType : Event.values()) {
            jsonArray.add(new JSONObject() {{
                put("originalName", eventType.name());
                put("displayName", eventType.toString());
            }});
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getErrorMessage() {
        final var jsonArray = new JSONArray();
        for (final var errorMessage : ErrorMessage.values()) {
            jsonArray.add(new JSONObject() {{
                put("originalName", errorMessage.name());
                put("displayName", errorMessage.toString());
            }});
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getUsageControlFramework() {
        final var jsonArray = new JSONArray();
        for (final var framework : UsageControlFramework.values()) {
            jsonArray.add(new JSONObject() {{
                put("originalName", framework.name());
                put("displayName", framework.toString());
            }});
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getActionType() {
        final var jsonArray = new JSONArray();
        for (final var actionType : ActionType.values()) {
            jsonArray.add(new JSONObject() {{
                put("originalName", actionType.name());
                put("displayName", actionType.toString());
                put("jsonInput", actionType.toString());
            }});
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getDataSourceType() {
        final var jsonArray = new JSONArray();
        for (final var type : DataSourceType.values()) {
            try {
                final var value = DataSourceType.class.getField(type.name())
                        .getAnnotation(JsonProperty.class).value();
                jsonArray.add(new JSONObject() {{
                    put("originalName", type.name());
                    put("displayName", value);
                    put("jsonInput", type.name());
                }});
            } catch (NoSuchFieldException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Missing JsonProperty found for dataSourceType.");
                }
            }
        }

        return sortJsonArray(jsonArray);
    }

    /**
     * Sort json array.
     *
     * @param jsonArray json array to be sorted.
     * @return sorted json array.
     */
    private static JSONArray sortJsonArray(final JSONArray jsonArray) {
        final var jsonObjects = new ArrayList<JSONObject>();

        for (final var o : jsonArray) {
            jsonObjects.add((JSONObject) o);
        }

        jsonObjects.sort(new Comparator<>() {
            private static final String KEY_NAME = "displayName";

            @Override
            public int compare(final JSONObject a, final JSONObject b) {
                var str1 = "";
                var str2 = "";
                try {
                    str1 = (String) a.get(KEY_NAME);
                    str2 = (String) b.get(KEY_NAME);
                } catch (JSONException e) {
                    if (log.isErrorEnabled()) {
                        log.error("Sorting contents of an array failed.");
                    }
                }
                return str1.compareTo(str2);
            }
        });

        var sortedJsonArray = new JSONArray();
        for (var i = 0; i < jsonArray.size(); i++) {
            sortedJsonArray.add(i, jsonObjects.get(i));
        }

        return sortedJsonArray;
    }
}
