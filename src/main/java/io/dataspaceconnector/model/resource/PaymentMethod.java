package io.dataspaceconnector.model.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Enumeration for resource payment modality.
 */
public enum PaymentMethod {

    /**
     * To express that the exchange of resource is with a fixed price.
     */
    @JsonProperty("fixedPrice")
    FIXED_PRICE,

    /**
     * To express that the exchange of resource is free.
     */
    @JsonProperty("free")
    FREE,

    /**
     * To express that the exchange of resource is negotiation-based.
     */
    @JsonProperty("negotiationBasis")
    NEGOTIATION_BASIS
}
