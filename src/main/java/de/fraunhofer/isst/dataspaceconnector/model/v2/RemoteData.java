package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.net.URI;

@lombok.Data
@Entity
@Table
@EqualsAndHashCode(callSuper = false)
@Setter(AccessLevel.PACKAGE)
public class RemoteData extends Data {
    private URI accessUrl;
    private String username;
    private String password;
}
