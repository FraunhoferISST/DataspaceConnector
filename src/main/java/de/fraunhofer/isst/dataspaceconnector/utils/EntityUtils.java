package de.fraunhofer.isst.dataspaceconnector.utils;

import java.net.URI;

public final class EntityUtils {

    private EntityUtils() {
        // not used
    }

    /**
     * Check if a parameter is empty.
     *
     * @param param The parameter.
     * @return True if it is empty, false if not.
     */
    public static boolean parameterIsEmpty(final URI param) {
        if (param == null) {
            return true;
        } else {
            return param.toString().equals("");
        }
    }
}
