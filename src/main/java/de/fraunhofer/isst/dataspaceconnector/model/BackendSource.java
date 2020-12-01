package de.fraunhofer.isst.dataspaceconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.net.URI;

/**
 * <p>BackendSource class.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Schema(
        name = "BackendSource",
        description = "Information of the backend system.",
        oneOf = BackendSource.class
)
public class BackendSource implements Serializable {
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

    @JsonProperty("type")
    private Type type;

    @JsonProperty("url")
    private URI url;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    /**
     * <p>Constructor for BackendSource.</p>
     */
    public BackendSource() {
    }

    /**
     * <p>Constructor for BackendSource.</p>
     *
     * @param type a {@link de.fraunhofer.isst.dataspaceconnector.model.BackendSource.Type} object.
     * @param url a {@link java.net.URI} object.
     * @param username a {@link java.lang.String} object.
     * @param password a {@link java.lang.String} object.
     */
    public BackendSource(Type type, URI url, String username, String password) {
        this.type = type;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link de.fraunhofer.isst.dataspaceconnector.model.BackendSource.Type} object.
     */
    public Type getType() {
        return type;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link de.fraunhofer.isst.dataspaceconnector.model.BackendSource.Type} object.
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * <p>Getter for the field <code>url</code>.</p>
     *
     * @return a {@link java.net.URI} object.
     */
    public URI getUrl() {
        return url;
    }

    /**
     * <p>Setter for the field <code>url</code>.</p>
     *
     * @param url a {@link java.net.URI} object.
     */
    public void setUrl(URI url) {
        this.url = url;
    }

    /**
     * <p>Getter for the field <code>username</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUsername() {
        return username;
    }

    /**
     * <p>Setter for the field <code>username</code>.</p>
     *
     * @param username a {@link java.lang.String} object.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * <p>Getter for the field <code>password</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPassword() {
        return password;
    }

    /**
     * <p>Setter for the field <code>password</code>.</p>
     *
     * @param password a {@link java.lang.String} object.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
