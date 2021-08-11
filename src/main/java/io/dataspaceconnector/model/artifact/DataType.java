package io.dataspaceconnector.model.artifact;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Enumeration class for data types.
 */
public enum DataType {

    /**
     * Data type is resource.
     */
    @JsonProperty("Resource")
    RESOURCE,

    /**
     * Data type is app template.
     */
    @JsonProperty("App Template")
    APP_TEMPLATE;
}
