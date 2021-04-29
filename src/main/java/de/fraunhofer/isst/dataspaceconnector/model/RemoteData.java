package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.net.URL;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Bundles information needed for accessing remote backends.
 */
@Entity
@Table
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class RemoteData extends Data {
    /**
     * Access url of the backend.
     */
    private URL accessUrl;

    /**
     * The username for accessing the backend.
     */
    private String username;

    /**
     * The password for accessing the backend.
     */
    private String password;
}
