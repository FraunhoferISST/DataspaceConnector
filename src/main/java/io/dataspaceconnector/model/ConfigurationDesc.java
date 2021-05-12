package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Describing the configuration's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConfigurationDesc extends AbstractDescription<Configuration> {

    /**
     * The log level.
     */
    private LogLevel logLevel;

    /**
     * The deploy mode of the connector.
     */
    private ConnectorDeployMode deployMode;

    /**
     * The proxy configuration.
     */
    private List<Proxy> proxy;

    /**
     * The trust store.
     */
    private String trustStore;

    /**
     * The password of the trust store.
     */
    private String trustStorePassword;

    /**
     * The key store.
     */
    private String keyStore;

    /**
     * The key store password.
     */
    private String keyStorePassword;
}
