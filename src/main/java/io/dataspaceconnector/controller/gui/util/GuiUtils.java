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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import java.util.Locale;

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
     * @param enumName name of the enum.
     * @return enums as string.
     */
    @SuppressFBWarnings("IMPROPER_UNICODE")
    public static String getSpecificEnum(final String enumName) {
        final var name = enumName.toLowerCase(Locale.ENGLISH);
        JSONArray sortedJsonArray = null;

        switch (name) {
            case "loglevel":
                sortedJsonArray = getLogLevel();
                break;
            case "connectorstatus":
                sortedJsonArray = getConnectorStatus();
                break;
            case "connectordeploymode":
                sortedJsonArray = getConnectorDeployMode();
                break;
            case "language":
                sortedJsonArray = getLanguage();
                break;
            case "deploymethod":
                sortedJsonArray = getDeployMethod();
                break;
            case "brokerstatus":
                sortedJsonArray = getBrokerStatus();
                break;
            case "securityprofile":
                sortedJsonArray = getSecurityProfile();
                break;
            case "paymentmethod":
                sortedJsonArray = getPaymentMethod();
                break;
            default:
                break;
        }

        return sortedJsonArray != null ? sortedJsonArray.toJSONString() : null;
    }

    private static JSONArray getPaymentMethod() {
        final var jsonArray = new JSONArray();
        final var paymentmethods = PaymentMethod.values();

        for (final var paymentmethod : paymentmethods) {
            try {
                var jsonObject = new JSONObject();
                jsonObject.put("originalName", paymentmethod.name());
                jsonObject.put("displayName", PaymentMethod.class.getField(paymentmethod.name())
                        .getAnnotation(JsonProperty.class).value());
                jsonObject.put("representation", paymentmethod.getRepresentation());
                jsonArray.add(jsonObject);
            } catch (NoSuchFieldException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Missing JsonProperty found for paymentmethod!");
                }
            }
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getSecurityProfile() {
        final var jsonArray = new JSONArray();
        final var securityProfiles = SecurityProfile.values();

        for (final var securityProfile : securityProfiles) {
            var jsonObject = new JSONObject();
            jsonObject.put("originalName", securityProfile.name());
            jsonObject.put("displayName", securityProfile.getLabel().get(0).getValue());
            jsonArray.add(jsonObject);
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getBrokerStatus() {
        final var jsonArray = new JSONArray();
        final var brokerStatuses = RegistrationStatus.values();

        for (final var brokerStatus : brokerStatuses) {
            var jsonObject = new JSONObject();
            jsonObject.put("displayName", brokerStatus.name());
            jsonArray.add(jsonObject);
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getDeployMethod() {
        final var jsonArray = new JSONArray();
        final var deployMethods = DeployMethod.values();

        for (final var deployMethod : deployMethods) {
            var jsonObject = new JSONObject();
            jsonObject.put("displayName", deployMethod.name());
            jsonArray.add(jsonObject);
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getLanguage() {
        final var jsonArray = new JSONArray();
        final var languages = Language.values();

        for (final var language : languages) {
            var jsonObject = new JSONObject();
            jsonObject.put("originalName", language.name());

            //Workaround for infomodel issue LT = LessThan
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
        final var connectorDeployModes = DeployMode.values();

        for (final var connectorDeployMode : connectorDeployModes) {
            try {
                var jsonObject = new JSONObject();
                jsonObject.put("originalName", connectorDeployMode.name());
                jsonObject.put("displayName", DeployMode.class.getField(connectorDeployMode.name())
                        .getAnnotation(JsonProperty.class).value());
                jsonArray.add(jsonObject);
            } catch (NoSuchFieldException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Missing JsonProperty found for connectorDeployMode!");
                }
            }
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getConnectorStatus() {
        final var jsonArray = new JSONArray();
        final var connectorStatuses = ConnectorStatus.values();

        for (final var connectorStatus : connectorStatuses) {
            var jsonObject = new JSONObject();
            jsonObject.put("originalName", connectorStatus.name());
            jsonObject.put("displayName", connectorStatus.getLabel().get(0).getValue());
            jsonArray.add(jsonObject);
        }

        return sortJsonArray(jsonArray);
    }

    private static JSONArray getLogLevel() {
        final var jsonArray = new JSONArray();
        final var logLevels = LogLevel.values();

        for (final var logLevel : logLevels) {
            try {
                var jsonObject = new JSONObject();
                jsonObject.put("originalName", logLevel.name());
                jsonObject.put("displayName", LogLevel.class.getField(logLevel.name())
                        .getAnnotation(JsonProperty.class).value());
                jsonArray.add(jsonObject);
            } catch (NoSuchFieldException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Missing JsonProperty found for logLevel!");
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
                        log.error("Sorting contents of an array for the GUI failed.");
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
