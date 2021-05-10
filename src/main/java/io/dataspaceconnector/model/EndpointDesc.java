package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EndpointDesc<T extends Endpoint> extends AbstractDescription<T> {
}
