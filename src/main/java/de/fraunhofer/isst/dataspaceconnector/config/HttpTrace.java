package de.fraunhofer.isst.dataspaceconnector.config;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class HttpTrace {
    public UUID id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    public LocalDateTime timestamp;
    public String method;
    public String uri;
    @JsonSerialize(using = RepresentationsToJson.class)
    public Map<String, String[]> parameterMap;
    public int status;
    public String body;

    @JsonAnyGetter
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

//    @Override
//    public String toString() {
//        ObjectMapper mapper = new ObjectMapper();
//        String jsonString = null;
//        try {
//            jsonString = mapper.writeValueAsString(this);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return jsonString;
//    }
//
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
}
