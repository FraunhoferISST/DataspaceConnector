package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URI;
import java.util.List;

/**
 * This class provides a model to handle data resource metadata.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Schema(
        name = "ResourceMetadata",
        description = "Metadata of a resource",
        oneOf = ResourceMetadata.class,
        example = "{\n" +
                "  \"title\": \"Sample Resource\",\n" +
                "  \"description\": \"This is an example resource containing weather data.\",\n" +
                "  \"keywords\": [\n" +
                "    \"weather\",\n" +
                "    \"data\",\n" +
                "    \"sample\"\n" +
                "  ],\n" +
                "  \"owner\": \"https://openweathermap.org/\",\n" +
                "  \"license\": \"ODbL\",\n" +
                "  \"version\": \"1.0\"\n" +
                "}\n"
)
public class ResourceMetadata implements Serializable {
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @ElementCollection
    @JsonProperty("keywords")
    private List<String> keywords;

    @Column(columnDefinition = "BYTEA")
    @JsonProperty("policy")
    private String policy;

    @JsonProperty("owner")
    private URI owner;

    @JsonProperty("license")
    private URI license;

    @JsonProperty("version")
    private String version;

    @NotNull
    @ElementCollection
    @Column(columnDefinition = "BYTEA")
    @JsonProperty("representations")
    private List<ResourceRepresentation> representations;

    /**
     * <p>Constructor for ResourceMetadata.</p>
     */
    public ResourceMetadata() {

    }

    /**
     * <p>Constructor for ResourceMetadata.</p>
     *
     * @param title a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @param keywords a {@link java.util.List} object.
     * @param policy a {@link java.lang.String} object.
     * @param owner a {@link java.net.URI} object.
     * @param license a {@link java.net.URI} object.
     * @param version a {@link java.lang.String} object.
     * @param representations a {@link java.util.List} object.
     */
    public ResourceMetadata(String title, String description, List<String> keywords, String policy,
                            URI owner, URI license, String version, List<ResourceRepresentation> representations) {
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.policy = policy;
        this.owner = owner;
        this.license = license;
        this.version = version;
        this.representations = representations;
    }

    /**
     * <p>Getter for the field <code>title</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTitle() {
        return title;
    }

    /**
     * <p>Setter for the field <code>title</code>.</p>
     *
     * @param title a {@link java.lang.String} object.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Getter for the field <code>keywords</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * <p>Setter for the field <code>keywords</code>.</p>
     *
     * @param keywords a {@link java.util.List} object.
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * <p>Getter for the field <code>policy</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPolicy() {
        return policy;
    }

    /**
     * <p>Setter for the field <code>policy</code>.</p>
     *
     * @param policy a {@link java.lang.String} object.
     */
    public void setPolicy(String policy) {
        this.policy = policy;
    }

    /**
     * <p>Getter for the field <code>owner</code>.</p>
     *
     * @return a {@link java.net.URI} object.
     */
    public URI getOwner() {
        return owner;
    }

    /**
     * <p>Setter for the field <code>owner</code>.</p>
     *
     * @param owner a {@link java.net.URI} object.
     */
    public void setOwner(URI owner) {
        this.owner = owner;
    }

    /**
     * <p>Getter for the field <code>license</code>.</p>
     *
     * @return a {@link java.net.URI} object.
     */
    public URI getLicense() {
        return license;
    }

    /**
     * <p>Setter for the field <code>license</code>.</p>
     *
     * @param license a {@link java.net.URI} object.
     */
    public void setLicense(URI license) {
        this.license = license;
    }

    /**
     * <p>Getter for the field <code>version</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getVersion() {
        return version;
    }

    /**
     * <p>Setter for the field <code>version</code>.</p>
     *
     * @param version a {@link java.lang.String} object.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * <p>Getter for the field <code>representations</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<ResourceRepresentation> getRepresentations() {
        return representations;
    }

    /**
     * <p>Setter for the field <code>representations</code>.</p>
     *
     * @param representations a {@link java.util.List} object.
     */
    public void setRepresentations(List<ResourceRepresentation> representations) {
        this.representations = representations;
    }

    /** {@inheritDoc} */
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
