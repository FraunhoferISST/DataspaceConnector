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

import de.fraunhofer.iais.eis.ConnectorDeployMode;
import de.fraunhofer.iais.eis.ConnectorStatus;
import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.iais.eis.LogLevel;
import io.configmanager.util.enums.BrokerRegistrationStatus;
import io.configmanager.util.enums.RouteDeployMethod;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.json.JSONException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The class can be used to define auxiliary methods that are needed again and again.
 */
@Slf4j
@Service
@Transactional
@NoArgsConstructor
public class GuiUtilService {

    /**
     * The method returns for a given enum name all enum values.
     *
     * @param enumName name of the enum
     * @return enums as string
     */
    public String getSpecificEnum(final String enumName) {
        final var name = enumName.toLowerCase();
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

        assert sortedJsonArray != null;
        return sortedJsonArray.toJSONString();
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
        final var connectorDeployModes = ConnectorDeployMode.values();

        for (final var connectorDeployMode : connectorDeployModes) {
            var jsonObject = new JSONObject();
            jsonObject.put("originalName", connectorDeployMode.name());
            jsonObject.put("displayName", connectorDeployMode.getLabel().get(0).getValue());
            jsonArray.add(jsonObject);
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
            var jsonObject = new JSONObject();
            jsonObject.put("originalName", logLevel.name());
            jsonObject.put("displayName", logLevel.getLabel().get(0).getValue());
            jsonArray.add(jsonObject);
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
                    log.error(e.getMessage(), e);
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
