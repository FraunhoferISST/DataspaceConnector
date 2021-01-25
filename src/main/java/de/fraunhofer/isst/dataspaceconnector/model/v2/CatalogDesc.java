package de.fraunhofer.isst.dataspaceconnector.model.v2;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class CatalogDesc extends BaseDescription<Catalog> {
    private String title;
    private String description;

    @JsonIgnore
    private Map<String, String> properties = new HashMap<>();

    @JsonAnySetter
    public void add(final String key, final String value) {
        properties.put(key, value);
    }
}
