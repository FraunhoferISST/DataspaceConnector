package io.dataspaceconnector.camel.util;

import java.util.HashMap;
import java.util.Map;

import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.util.IdsUtils;

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
        map.put(ParameterUtils.HEADER_PART_NAME, IdsUtils.toRdf(response.getHeader()));
        map.put(ParameterUtils.PAYLOAD_PART_NAME, response.getBody());
        return map;
    }

}
