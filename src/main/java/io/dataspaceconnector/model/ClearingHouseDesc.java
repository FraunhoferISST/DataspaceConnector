package io.dataspaceconnector.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.net.URI;
/**
 * Describing the clearing house's properties.
 */
@Data
@NoArgsConstructor
public class ClearingHouseDesc extends AbstractDescription<ClearingHouse> {

    /**
     * The access url of the clearing house.
     */
    private URI accessUrl;

    /**
     * The title of the clearing house.
     */
    private String title;

    /**
     * The status of registration.
     */
    private RegisterStatus registerStatus;
}
