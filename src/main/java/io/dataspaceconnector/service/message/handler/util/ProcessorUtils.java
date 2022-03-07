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
package io.dataspaceconnector.service.message.handler.util;

import io.dataspaceconnector.common.ids.mapping.RdfConverter;
import io.dataspaceconnector.common.routing.ParameterUtils;
import io.dataspaceconnector.service.message.handler.dto.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains utility methods used by different processors.
 */
public final class ProcessorUtils {

    /**
     * Private constructor for utility class.
     */
    private ProcessorUtils() { }

    /**
     * Creates a map containing header and payload as String from a response.
     *
     * @param response the response.
     * @return the map.
     */
    public static Map<String, String> getResponseMap(final Response response) {
        final var map = new HashMap<String, String>();
        map.put(ParameterUtils.HEADER_PART_NAME, RdfConverter.toRdf(response.getHeader()));
        map.put(ParameterUtils.PAYLOAD_PART_NAME, response.getBody());
        return map;
    }

}
