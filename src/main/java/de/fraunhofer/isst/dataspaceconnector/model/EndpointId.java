package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Describes an endpoint splitting it into the general path and the
 * id of the resource affected by this endpoint.
 */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class EndpointId implements Serializable {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The base path.
     */
    @JsonIgnore
    private String basePath;

    /**
     * The uuid.
     */
    @JsonIgnore
    private UUID resourceId;
}
