package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.Data;

import java.util.UUID;

/**
 * The base class for all descriptions.
 * @param <T> The type of the class described by the description.
 */
@Data
public class BaseDescription<T> {
    /**
     * The static id assigned to public endpoints.
     */
    private UUID staticId;
}
