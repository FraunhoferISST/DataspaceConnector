package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.net.URI;

/**
 * BackendSource class.
 */
@Schema(
    name = "BackendSource",
    description = "Information of the backend system.",
    oneOf = BackendSource.class
)
@JsonInclude(Include.NON_NULL)
public class BackendSource implements Serializable {

    @JsonProperty("type")
    private Type type;
    @JsonProperty("url")
    private URI url;
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
     */
    public BackendSource(Type type, URI url, String username, String password) {
        this.type = type;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Getter for the field type.
     *
     * @return a {@link de.fraunhofer.isst.dataspaceconnector.model.BackendSource.Type} object.
     */
    public Type getType() {
        return type;
    }

    /**
     * Setter for the field type.
     *
     * @param type a {@link de.fraunhofer.isst.dataspaceconnector.model.BackendSource.Type} object.
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Getter for the field url.
     *
     * @return a {@link java.net.URI} object.
     */
    public URI getUrl() {
        return url;
    }

    /**
     * Setter for the field url.
     *
     * @param url a {@link java.net.URI} object.
     */
    public void setUrl(URI url) {
        this.url = url;
    }

    /**
     * Getter for the field username.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for the field username.
     *
     * @param username a {@link java.lang.String} object.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter for the field password.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for the field password.
     *
     * @param password a {@link java.lang.String} object.
     */
    public void setPassword(String password) {
        this.password = password;
    }

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
