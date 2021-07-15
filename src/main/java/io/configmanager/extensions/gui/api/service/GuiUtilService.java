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
/*
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
package io.configmanager.extensions.gui.api.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.fraunhofer.iais.eis.ConnectorStatus;
import de.fraunhofer.iais.eis.Language;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.configmanager.util.enums.BrokerRegistrationStatus;
import io.configmanager.util.enums.RouteDeployMethod;
import io.dataspaceconnector.model.configuration.DeployMode;
import io.dataspaceconnector.model.configuration.LogLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.json.JSONException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * The class can be used to define auxiliary methods that are needed again and again.
 */
@Log4j2
@Service
@Transactional
@NoArgsConstructor
@SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON",
        justification = "GuiUtilService is a service-class for the GuiUtilController and too"
                + " big to become a static inner class in GuiUtilController.")
public class GuiUtilService {

    /**
     * The method returns for a given enum name all enum values.
     *
     * @param enumName name of the enum
     * @return enums as string
     */
    @SuppressFBWarnings("IMPROPER_UNICODE")
    public String getSpecificEnum(final String enumName) {
        final var name = enumName.toLowerCase(Locale.ENGLISH);
        JSONArray sortedJsonArray = null;

        switch (name) {
            case "loglevel": sortedJsonArray = getLogLevel(); break;
            case "connectorstatus": sortedJsonArray = getConnectorStatus(); break;
            case "connectordeploymode": sortedJsonArray = getConnectorDeployMode(); break;
            case "language": sortedJsonArray = getLanguage(); break;
            case "deploymethod": sortedJsonArray = getDeployMethod(); break;
            case "brokerstatus": sortedJsonArray = getBrokerStatus(); break;
            default: break;
        }

        return sortedJsonArray != null ? sortedJsonArray.toJSONString() : null;
    }

    private JSONArray getBrokerStatus() {
        JSONArray sortedJsonArray;

        final var jsonArray = new JSONArray();
        final var brokerStatuses = BrokerRegistrationStatus.values();

        for (final var brokerStatus : brokerStatuses) {
            var jsonObject = new JSONObject();
            jsonObject.put("displayName", brokerStatus.name());
            jsonArray.add(jsonObject);
        }

        sortedJsonArray = sortJsonArray(jsonArray);
        return sortedJsonArray;
    }

    private JSONArray getDeployMethod() {
        JSONArray sortedJsonArray;

        final var jsonArray = new JSONArray();
        final var deployMethods = RouteDeployMethod.values();

        for (final var deployMethod : deployMethods) {
            var jsonObject = new JSONObject();
            jsonObject.put("displayName", deployMethod.name());
            jsonArray.add(jsonObject);
        }

        sortedJsonArray = sortJsonArray(jsonArray);
        return sortedJsonArray;
    }

    private JSONArray getLanguage() {
        JSONArray sortedJsonArray;

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

        sortedJsonArray = sortJsonArray(jsonArray);
        return sortedJsonArray;
    }

    private JSONArray getConnectorDeployMode() {
        JSONArray sortedJsonArray;

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
                log.debug("Missing JsonProperty found for connectorDeployMode!");
            }
        }

        sortedJsonArray = sortJsonArray(jsonArray);
        return sortedJsonArray;
    }

    private JSONArray getConnectorStatus() {
        JSONArray sortedJsonArray;

        final var jsonArray = new JSONArray();
        final var connectorStatuses = ConnectorStatus.values();

        for (final var connectorStatus : connectorStatuses) {
            var jsonObject = new JSONObject();
            jsonObject.put("originalName", connectorStatus.name());
            jsonObject.put("displayName", connectorStatus.getLabel().get(0).getValue());
            jsonArray.add(jsonObject);
        }

        sortedJsonArray = sortJsonArray(jsonArray);
        return sortedJsonArray;
    }

    private JSONArray getLogLevel() {
        JSONArray sortedJsonArray;

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
                log.debug("Missing JsonProperty found for logLevel!");
            }
        }

        sortedJsonArray = sortJsonArray(jsonArray);
        return sortedJsonArray;
    }

    /**
     * @param jsonArray json array to be sorted
     * @return sorted json array
     */
    private JSONArray sortJsonArray(final JSONArray jsonArray) {
        List<JSONObject> jsonObjects = new ArrayList<>();

        var sortedJsonArray = new JSONArray();

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

        for (var i = 0; i < jsonArray.size(); i++) {
            sortedJsonArray.add(i, jsonObjects.get(i));
        }

        return sortedJsonArray;
    }
}
