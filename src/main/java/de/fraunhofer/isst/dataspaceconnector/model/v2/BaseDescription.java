package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.Data;

import java.util.UUID;

@Data
public class BaseDescription<T> {
    private UUID staticId;
}
