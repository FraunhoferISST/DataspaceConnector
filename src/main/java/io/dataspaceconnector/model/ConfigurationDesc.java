package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConfigurationDesc extends AbstractDescription<Configuration>{

    private LogLevel logLevel;

    private ConnectorDeployMode deployMode;

    private List<Proxy> proxy;

    private String trustStore;

    private String trustStorePassword;

    private String keyStore;

    private String keyStorePassword;
}
