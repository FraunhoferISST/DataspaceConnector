package io.dataspaceconnector.common.ids.mapping;

import de.fraunhofer.iais.eis.ConnectorEndpoint;
import de.fraunhofer.iais.eis.util.TypedLiteral;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MappingUtils {


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
