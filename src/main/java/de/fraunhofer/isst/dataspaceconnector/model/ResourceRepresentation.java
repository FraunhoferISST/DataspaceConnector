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

/**
 * <p>ResourceRepresentation class.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Schema(
    name = "ResourceRepresentation",
    description = "Representation of a resource",
    oneOf = ResourceRepresentation.class,
    example = "{\"uuid\":\"55795317-0aaa-4fe1-b336-b2e26a00597f\",\"type\":\"XML\",\"byteSize\":101,\"name\":\"Example Representation\",\"source\":{\"type\":\"local\"}}"
)
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
     * <p>Constructor for ResourceRepresentation.</p>
     */
    public ResourceRepresentation() {
    }

    /**
     * <p>Constructor for ResourceRepresentation.</p>
     *
     * @param uuid     a {@link java.util.UUID} object.
     * @param type     a {@link java.lang.String} object.
     * @param byteSize a {@link java.lang.Integer} object.
     * @param name     a {@link java.lang.String} object.
     * @param source   a {@link de.fraunhofer.isst.dataspaceconnector.model.BackendSource} object.
     */
    public ResourceRepresentation(UUID uuid, String type, Integer byteSize, String name,
        BackendSource source) {
        this.uuid = uuid;
        this.type = type;
        this.byteSize = byteSize;
        this.name = name;
        this.source = source;
    }

    /**
     * <p>Getter for the field <code>uuid</code>.</p>
     *
     * @return a {@link java.util.UUID} object.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * <p>Setter for the field <code>uuid</code>.</p>
     *
     * @param uuid a {@link java.util.UUID} object.
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getType() {
        return type;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link java.lang.String} object.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * <p>Getter for the field <code>byteSize</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getByteSize() {
        return byteSize;
    }

    /**
     * <p>Setter for the field <code>byteSize</code>.</p>
     *
     * @param byteSize a {@link java.lang.Integer} object.
     */
    public void setByteSize(Integer byteSize) {
        this.byteSize = byteSize;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>source</code>.</p>
     *
     * @return a {@link de.fraunhofer.isst.dataspaceconnector.model.BackendSource} object.
     */
    public BackendSource getSource() {
        return source;
    }

    /**
     * <p>Setter for the field <code>source</code>.</p>
     *
     * @param source a {@link de.fraunhofer.isst.dataspaceconnector.model.BackendSource} object.
     */
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
