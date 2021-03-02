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
import lombok.Data;

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
    example = "{\"title\":\"ExampleResource\",\"description\":\"ExampleResourceDescription\",\"policy\":\"Example policy\",\"representations\":[{\"type\":\"XML\",\"byteSize\":101,\"name\":\"Example Representation\",\"source\":{\"type\":\"local\"}}]}"
)
@Data
@JsonInclude(Include.NON_NULL)
public class ResourceMetadata implements Serializable {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

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

    @JsonProperty("endpointDocumentation")
    private URI endpointDocumentation;

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
     *
     * @param title The title of the resource
     * @param description The description of the resource
     * @param keywords Keywords associated with the resource
     * @param policy The policy applied to the resource
     * @param owner The owner of this resource
     * @param license The licence under which this resource is publised
     * @param version The version of the resource
     * @param representations The representations of the resource
     */
    public ResourceMetadata(String title, String description, List<String> keywords, String policy,
        URI owner, URI license, String version, Map<UUID, ResourceRepresentation> representations,
        URI endpointDocumentation) {
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.policy = policy;
        this.owner = owner;
        this.license = license;
        this.version = version;
        this.representations = representations;
        this.endpointDocumentation = endpointDocumentation;
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
                    output.put(representation.getUuid() == null ? UUID.randomUUID() : representation.getUuid(), representation);
                }

                return output;
            } catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }
}
