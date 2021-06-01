package io.dataspaceconnector.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.dataspaceconnector.model.RegisterStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.net.URI;
import java.time.ZonedDateTime;

/**
 * A DTO for controlled exposing of generic endpoint information in API responses.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = "identityproviders", itemRelation = "identityprovider")
public class IdentityProviderView extends RepresentationModel<IdentityProviderView> {

    /**
     * The creation date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime creationDate;

    /**
     * The last modification date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime modificationDate;

    /**
     * The access url of the identity provider.
     */
    private URI accessUrl;

    /**
     * The title of the identity provider.
     */
    private String title;

    /**
     * The registration status.
     */
    private RegisterStatus registerStatus;
}
