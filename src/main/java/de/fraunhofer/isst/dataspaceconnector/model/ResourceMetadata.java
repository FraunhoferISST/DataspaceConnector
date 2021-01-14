package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class provides a model to handle data resource metadata.
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
     * Constructor for ResourceMetadata.
     */
    public ResourceMetadata() {

    }

    /**
     * Constructor with parameters for ResourceMetadata.
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public URI getOwner() {
        return owner;
    }

    public void setOwner(URI owner) {
        this.owner = owner;
    }

    public URI getLicense() {
        return license;
    }

    public void setLicense(URI license) {
        this.license = license;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<UUID, ResourceRepresentation> getRepresentations() {
        return representations;
    }

    public void setRepresentations(Map<UUID, ResourceRepresentation> representations) {
        this.representations = representations;
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
