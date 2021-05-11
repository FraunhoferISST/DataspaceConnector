package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URL;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConnectorDesc extends AbstractDescription<Connector> {

    private URL accessUrl;

    private String title;

    private RegisterStatus registerStatus;

}
