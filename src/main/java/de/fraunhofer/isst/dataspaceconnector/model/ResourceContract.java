package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * This class provides a model to handle agreed resource contracts.
 */
@Entity
@Table
@Getter
@Setter(AccessLevel.PUBLIC)
@EqualsAndHashCode
@RequiredArgsConstructor
public class ResourceContract {
    @Id
    @JsonProperty("uuid")
    private UUID id;

    @JsonProperty("contract")
    @Lob
    private String contract;
}
