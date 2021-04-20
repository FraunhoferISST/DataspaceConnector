package de.fraunhofer.isst.dataspaceconnector.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = "contract", itemRelation = "contract")
public class ContractView  extends RepresentationModel<ContractView> {
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
     * The title of the contract.
     */
    private String title;

    /**
     * The start date of the contract.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime start;

    /**
     * The end data of the contract.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime end;

    /**
     * The consumer addressed by the contract offer.
     */
    private URI consumer;

    /**
     * Additional properties.
     */
    private Map<String, String> additional;
}
