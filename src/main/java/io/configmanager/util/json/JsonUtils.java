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
package io.configmanager.util.json;

import lombok.experimental.UtilityClass;
import net.minidev.json.JSONObject;

/**
 * Utility class which can be used to define helper methods.
 */
@UtilityClass
public class JsonUtils {
    /**
     * This method creates with the given parameters a JSON message.
     *
     * @param key   the key of the json object
     * @param value the value of the json object
     * @return json message
     */
    public static String jsonMessage(final String key, final String value) {
        final var jsonObject = new JSONObject();
        jsonObject.put(key, value);

        return jsonObject.toJSONString();
    }
}
