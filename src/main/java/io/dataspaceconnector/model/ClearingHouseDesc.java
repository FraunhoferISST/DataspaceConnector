package io.dataspaceconnector.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@NoArgsConstructor
public class ClearingHouseDesc extends AbstractDescription<ClearingHouse> {

    private URI accessUrl;

    private String title;

    private RegisterStatus registerStatus;
}
