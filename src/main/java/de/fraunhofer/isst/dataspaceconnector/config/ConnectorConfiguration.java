package de.fraunhofer.isst.dataspaceconnector.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

/**
 * This class handles policy settings: negotiation, pattern support, and usage control framework.
 */
@Data
@Configuration
public class ConnectorConfiguration {
    /**
     * The clearing house access url.
     */
    @Value("${clearing.house.url}")
    private URI clearingHouse;

    /**
     * The policy negotiation status from application.properties.
     */
    @Value("${policy.negotiation}")
    private boolean policyNegotiation;

    /**
     * Setting for allowing unsupported patterns from application.properties.
     */
    @Value("${policy.allow-unsupported-patterns}")
    private boolean allowUnsupported;

    /**
     * Usage control framework from application.properties.
     */
    @Value("${policy.framework}")
    private UsageControlFramework ucFramework;
}
