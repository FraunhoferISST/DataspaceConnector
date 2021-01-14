package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/** ResourceRepresentation class. */
@Schema(
    name = "ResourceRepresentation",
    description = "Representation of a resource",
    oneOf = ResourceRepresentation.class,
    example =
        "{\"uuid\":\"55795317-0aaa-4fe1-b336-b2e26a00597f\",\"type\":\"JSON\",\"byteSize\":101,\"name\":\"Example Representation\",\"source\":{\"type\":\"http-get\",\"url\":\"https://samples.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=439d4b804bc8187953eb36d2a8c26a02\"}}")
@JsonInclude(Include.NON_NULL)
public class ResourceRepresentation implements Serializable {

    @Id
    @JsonProperty("uuid")
    private UUID uuid;

    @JsonProperty("type")
    private String type;

    @JsonProperty("byteSize")
    private Integer byteSize;

    @JsonProperty("name")
    private String name;

    @JsonProperty("source")
    @Column(columnDefinition = "BLOB")
    private BackendSource source;

    /**
     * Constructor for ResourceRepresentation.
     */
    public ResourceRepresentation() {
    }

    /**
     * Constructor with parameters for ResourceRepresentation.
     */
    public ResourceRepresentation(UUID uuid, String type, Integer byteSize, String name,
        BackendSource source) {
        this.uuid = uuid;
        this.type = type;
        this.byteSize = byteSize;
        this.name = name;
        this.source = source;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getByteSize() {
        return byteSize;
    }

    public void setByteSize(Integer byteSize) {
        this.byteSize = byteSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BackendSource getSource() {
        return source;
    }

    public void setSource(BackendSource source) {
        this.source = source;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
