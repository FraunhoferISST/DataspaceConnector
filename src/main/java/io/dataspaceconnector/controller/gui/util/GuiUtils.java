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
import de.fraunhofer.iais.eis.ConnectorStatus;
import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.iais.eis.SecurityProfile;
import io.dataspaceconnector.model.base.RegistrationStatus;
import io.dataspaceconnector.model.configuration.DeployMethod;
import io.dataspaceconnector.model.configuration.DeployMode;
import io.dataspaceconnector.model.configuration.LogLevel;
import io.dataspaceconnector.model.resource.PaymentMethod;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
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
     * The method returns for a given enum name all enum values.
     *
     * @param type name of the enum.
     * @return enums as string.
     */
    public static JSONObject getListOfEnumsByType(final EnumType type) {
        var object = new JSONObject();

        switch (type) {
            case LOG_LEVEL:
                object.put(EnumType.LOG_LEVEL.toString(), getLogLevel());
                break;
            case CONNECTOR_STATUS:
                object.put(EnumType.CONNECTOR_STATUS.toString(), getConnectorStatus());
                break;
            case CONNECTOR_DEPLOY_MODE:
                object.put(EnumType.CONNECTOR_DEPLOY_MODE.toString(), getConnectorDeployMode());
                break;
            case LANGUAGE:
                object.put(EnumType.LANGUAGE.toString(), getLanguage());
                break;
            case DEPLOY_METHOD:
                object.put(EnumType.DEPLOY_METHOD.toString(), getDeployMethod());
                break;
            case BROKER_STATUS:
                object.put(EnumType.BROKER_STATUS.toString(), getBrokerStatus());
                break;
            case SECURITY_PROFILE:
                object.put(EnumType.SECURITY_PROFILE.toString(), getSecurityProfile());
                break;
            case PAYMENT_METHOD:
                object.put(EnumType.PAYMENT_METHOD.toString(), getPaymentMethod());
                break;
            default:
                break;
        }

        return object;
    }

    private static JSONArray getPaymentMethod() {
        final var jsonArray = new JSONArray();
        for (final var paymentMethod : PaymentMethod.values()) {
            try {
                jsonArray.add(new JSONObject() {{
                    put("originalName", paymentMethod.name());
                    put("displayName", PaymentMethod.class.getField(paymentMethod.name())
                            .getAnnotation(JsonProperty.class).value());
                    put("representation", paymentMethod.getRepresentation());
                }});
            } catch (NoSuchFieldException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Missing JsonProperty found for paymentMethod.");
                }
            }
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getSecurityProfile() {
        final var jsonArray = new JSONArray();
        for (final var securityProfile : SecurityProfile.values()) {
            jsonArray.add(new JSONObject() {{
                put("originalName", securityProfile.name());
                put("displayName", securityProfile.getLabel().get(0).getValue());
            }});
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getBrokerStatus() {
        final var jsonArray = new JSONArray();
        for (final var brokerStatus : RegistrationStatus.values()) {
            jsonArray.add(new JSONObject() {{
                put("displayName", brokerStatus.name());
            }});
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getDeployMethod() {
        final var jsonArray = new JSONArray();
        for (final var deployMethod : DeployMethod.values()) {
            jsonArray.add(new JSONObject() {{
                put("displayName", deployMethod.name());
            }});
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getLanguage() {
        final var jsonArray = new JSONArray();
        for (final var language : Language.values()) {
            var jsonObject = new JSONObject();
            jsonObject.put("originalName", language.name());

            // Workaround for Infomodel issue LT = LessThan
            if ("LT".equals(language.name())) {
                jsonObject.put("displayName", language.getLabel().get(1).getValue());
            } else {
                jsonObject.put("displayName", language.getLabel().get(0).getValue());
            }
            jsonArray.add(jsonObject);
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getConnectorDeployMode() {
        final var jsonArray = new JSONArray();
        for (final var connectorDeployMode : DeployMode.values()) {
            try {
                jsonArray.add(new JSONObject() {{
                    put("originalName", connectorDeployMode.name());
                    put("displayName", DeployMode.class.getField(connectorDeployMode.name())
                            .getAnnotation(JsonProperty.class).value());
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
                jsonArray.add(new JSONObject() {{
                    put("originalName", logLevel.name());
                    put("displayName", LogLevel.class.getField(logLevel.name())
                            .getAnnotation(JsonProperty.class).value());
                }});
            } catch (NoSuchFieldException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Missing JsonProperty found for logLevel.");
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
