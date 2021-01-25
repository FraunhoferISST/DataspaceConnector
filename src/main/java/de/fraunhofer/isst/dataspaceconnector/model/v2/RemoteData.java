package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.AccessLevel;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.net.URI;

@lombok.Data
@Entity
@Table
@Setter(AccessLevel.PACKAGE)
public class RemoteData extends Data {
    private URI accessUrl;
    private String username;
    private String password;
}
