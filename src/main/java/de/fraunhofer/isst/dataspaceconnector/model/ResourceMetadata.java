package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class provides a model to handle data resource metadata.
 *
 * @version $Id: $Id
 */
@Schema(
    name = "ResourceMetadata",
    description = "Metadata of a resource",
    oneOf = ResourceMetadata.class,
    example = "{\"title\":\"ExampleResource\",\"description\":\"ExampleResourceDescription\",\"policy\":\"Example policy\",\"representations\":[{\"uuid\":\"8e3a5056-1e46-42e1-a1c3-37aa08b2aedd\",\"type\":\"XML\",\"byteSize\":101,\"name\":\"Example Representation\",\"source\":{\"type\":\"local\"}}]}"
)
@JsonInclude(Include.NON_NULL)
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
    @JsonSerialize(using = RepresentationsToJson.class)
    @JsonDeserialize(using = JsonToRepresentation.class)
    private Map<UUID, ResourceRepresentation> representations;

    /**
     * <p>Constructor for ResourceMetadata.</p>
     */
    public ResourceMetadata() {

    }

    /**
     * <p>Constructor for ResourceMetadata.</p>
     *
     * @param title           a {@link java.lang.String} object.
     * @param description     a {@link java.lang.String} object.
     * @param keywords        a {@link java.util.List} object.
     * @param policy          a {@link java.lang.String} object.
     * @param owner           a {@link java.net.URI} object.
     * @param license         a {@link java.net.URI} object.
     * @param version         a {@link java.lang.String} object.
     * @param representations a {@link java.util.List} object.
     */
    public ResourceMetadata(String title, String description, List<String> keywords, String policy,
        URI owner, URI license, String version, Map<UUID, ResourceRepresentation> representations) {
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
    public Map<UUID, ResourceRepresentation> getRepresentations() {
        return representations;
    }

    /**
     * <p>Setter for the field <code>representations</code>.</p>
     *
     * @param representations a {@link java.util.List} object.
     */
    public void setRepresentations(Map<UUID, ResourceRepresentation> representations) {
        this.representations = representations;
    }

    /**
     * {@inheritDoc}
     */
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

    private static class RepresentationsToJson extends
        JsonSerializer<Map<UUID, ResourceRepresentation>> {

        @Override
        public void serialize(Map<UUID, ResourceRepresentation> value, JsonGenerator gen,
            SerializerProvider provider) {
            try {
                gen.writeObject(value.values());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private static class JsonToRepresentation extends
        JsonDeserializer<Map<UUID, ResourceRepresentation>> {

        @Override
        public Map<UUID, ResourceRepresentation> deserialize(JsonParser p,
            DeserializationContext ctx) {
            try {
                var node = p.readValueAsTree();
                final var objectMapper = new ObjectMapper();

                var representations = IntStream.range(0, node.size()).boxed()
                    .map(i -> {
                        try {
                            return objectMapper
                                .readValue(node.get(i).toString(), ResourceRepresentation.class);
                        } catch (IOException e) {
                            throw new RuntimeException();
                        }
                    }).collect(Collectors.toList());

                var output = new HashMap<UUID, ResourceRepresentation>();
                for (var representation : representations) {
                    output.put(representation.getUuid(), representation);
                }

                return output;
            } catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }
}
