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
    @JsonProperty("url")
    private URI url;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("system")
    private String system;

    /**
     * <p>Constructor for BackendSource.</p>
     */
    public BackendSource() {
    }

    /**
     * <p>Constructor for BackendSource.</p>
     *
     * @param url a {@link java.net.URI} object.
     * @param username a {@link java.lang.String} object.
     * @param password a {@link java.lang.String} object.
     * @param system a {@link java.lang.String} object.
     */
    public BackendSource(URI url, String username, String password, String system) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.system = system;
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

    /**
     * <p>Getter for the field <code>system</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSystem() {
        return system;
    }

    /**
     * <p>Setter for the field <code>system</code>.</p>
     *
     * @param system a {@link java.lang.String} object.
     */
    public void setSystem(String system) {
        this.system = system;
    }
}
