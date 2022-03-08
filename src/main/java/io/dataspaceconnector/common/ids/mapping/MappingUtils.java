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
package io.dataspaceconnector.common.ids.mapping;

import de.fraunhofer.iais.eis.ConnectorEndpoint;
import de.fraunhofer.iais.eis.util.TypedLiteral;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Provide mapping utils.
 */
public final class MappingUtils {

    /**
     * Utility class does not have to be instantiated.
     */
    private MappingUtils() {
    }


    /**
     * Get list of ids keywords as list of strings. If the list is null, an empty list is returned.
     *
     * @param keywords List of typed literals.
     * @return List of strings.
     */
    public static List<String> getKeywordsAsString(final List<? extends TypedLiteral> keywords) {
        final var list = new ArrayList<String>();
        if (keywords != null && !keywords.isEmpty()) {
            for (final var keyword : keywords) {
                list.add(keyword.getValue());
            }
        }

        return list;
    }

    /**
     * Returns the first endpoint documentations of the first endpoint.
     *
     * @param endpoints The list of endpoints.
     * @return The endpoint documentation.
     */
    public static Optional<URI> getFirstEndpointDocumentation(
            final List<? extends ConnectorEndpoint> endpoints) {
        Optional<URI> output = Optional.empty();

        if (endpoints != null && !endpoints.isEmpty()) {
            final var first = endpoints.get(0);

            if (first.getEndpointDocumentation() != null
                    && !first.getEndpointDocumentation().isEmpty()) {
                output = Optional.of(first.getEndpointDocumentation().get(0));
            }
        }

        return output;
    }
}
