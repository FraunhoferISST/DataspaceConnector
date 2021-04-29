package de.fraunhofer.isst.dataspaceconnector.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * The base class for all descriptions.
 * @param <T> The type of the class described by the description.
 */
@Data
public class AbstractDescription<T> {
    /**
     * The overflow for all elements that cannot be mapped.
     */
    @JsonIgnore
    private Map<String, String> additional;

    /**
     * Add a value to the overflow field.
     * If the key already exists it will be overwritten.
     * @param key The key.
     * @param value The value.
     */
    @JsonAnySetter
    public void addOverflow(final String key, final String value) {
        if (additional == null) {
            additional = new HashMap<>();
        }

        additional.put(key, value);
    }
}
