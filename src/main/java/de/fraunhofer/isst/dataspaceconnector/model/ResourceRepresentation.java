package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
        example = "{\n" +
                "      \"type\": \"json\",\n" +
                "      \"byteSize\": 105,\n" +
                "      \"sourceType\": \"http-get\",\n" +
                "      \"source\": {\n" +
                "        \"url\": \"https://samples.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=439d4b804bc8187953eb36d2a8c26a02\",\n" +
                "        \"username\": \"-\",\n" +
                "        \"password\": \"-\"\n" +
                "      },\n" +
                "      \"name\": \"Open Weather Map API\"\n" +
                "    }"
)
public class ResourceRepresentation implements Serializable {
    @Schema(
            name = "SourceType",
            description = "Information of the backend system.",
            oneOf = SourceType.class
    )
    public enum SourceType {
        @JsonProperty("local")
        LOCAL("local"),
        @JsonProperty("http-get")
        HTTP_GET("http-get"),
        @JsonProperty("http-get-basicauth")
        HTTP_GET_BASICAUTH("http-get-basicauth"),
        @JsonProperty("https-get")
        HTTPS_GET("https-get"),
        @JsonProperty("https-get-basicauth")
        HTTPS_GET_BASICAUTH("https-get-basicauth"),
        @JsonProperty("mongodb")
        MONGODB("mongodb");

        private final String type;

        SourceType(String string) {
            type = string;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    @Id
    @JsonProperty("uuid")
    private UUID uuid;

    @JsonProperty("type")
    private String type;

    @JsonProperty("byteSize")
    private Integer byteSize;

    @JsonProperty("sourceType")
    private SourceType sourceType;

    @JsonProperty("source")
    @Column(columnDefinition = "BLOB")
    private BackendSource source;

    @JsonProperty("name")
    private String name;

    /**
     * <p>Constructor for ResourceRepresentation.</p>
     */
    public ResourceRepresentation() {
    }

    /**
     * <p>Constructor for ResourceRepresentation.</p>
     *
     * @param uuid       a {@link java.util.UUID} object.
     * @param type       a {@link java.lang.String} object.
     * @param byteSize   a {@link java.lang.Integer} object.
     * @param sourceType a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation.SourceType} object.
     * @param source     a {@link de.fraunhofer.isst.dataspaceconnector.model.BackendSource} object.
     * @param name       a {@link java.lang.String} object.
     */
    public ResourceRepresentation(UUID uuid, String type, Integer byteSize, SourceType sourceType, BackendSource source, String name) {
        this.uuid = uuid;
        this.type = type;
        this.byteSize = byteSize;
        this.sourceType = sourceType;
        this.source = source;
        this.name = name;
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
     * <p>Getter for the field <code>sourceType</code>.</p>
     *
     * @return a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation.SourceType} object.
     */
    public SourceType getSourceType() {
        return sourceType;
    }

    /**
     * <p>Setter for the field <code>sourceType</code>.</p>
     *
     * @param sourceType a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation.SourceType} object.
     */
    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
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
}
