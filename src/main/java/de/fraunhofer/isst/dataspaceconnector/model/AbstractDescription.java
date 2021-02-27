package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The base class for all descriptions.
 * @param <T> The type of the class described by the description.
 */
@Data
public class AbstractDescription<T> {
    /**
     * The static id assigned to public endpoints.
     */
    private UUID staticId;

    @JsonIgnore
    private Map<String, String> additional;

    @JsonAnySetter
    public void set(final String key, final String value) {
        if(additional == null)
            additional = new HashMap<String, String>();

        additional.put(key, value);
    }
}
