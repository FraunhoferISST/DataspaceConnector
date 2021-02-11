package de.fraunhofer.isst.dataspaceconnector.model;

import lombok.Data;

import java.util.UUID;

@Data
public class BaseDescription<T> {
    private UUID staticId;
}
