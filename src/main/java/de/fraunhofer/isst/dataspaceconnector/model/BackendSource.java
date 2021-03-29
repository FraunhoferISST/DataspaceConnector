package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.net.URL;

/**
 * This class is used to describe the resource source in the backend.
 */
@Schema(
    name = "BackendSource",
    description = "Information of the backend system.",
    oneOf = BackendSource.class
)
@Data
@JsonInclude(Include.NON_NULL)
public class BackendSource implements Serializable {
    //Default serial version uid
    private static final long serialVersionUID = 1L;

    @JsonProperty("type")
    private Type type;
    @JsonProperty("url")
    private URL url;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;

    /**
     * Constructor for BackendSource.
     */
    public BackendSource() {
    }

    /**
     * Constructor with parameters for BackendSource.
     *
     * @param type The backend type
     * @param url The access url of the backend
     * @param username The username for authentication
     * @param password The password for authentication
     */
    public BackendSource(Type type, URL url, String username, String password) {
        this.type = type;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * This enum is used to describe how the backend is accessed.
     */
    @Schema(
        name = "Type",
        description = "Information of the backend system.",
        oneOf = Type.class
    )
    public enum Type {
        @JsonProperty("local")
        LOCAL("local"),
        @JsonProperty("http-get")
        HTTP_GET("http-get"),
        @JsonProperty("https-get")
        HTTPS_GET("https-get"),
        @JsonProperty("https-get-basicauth")
        HTTPS_GET_BASICAUTH("https-get-basicauth");

        private final String type;

        Type(String string) {
            type = string;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}
