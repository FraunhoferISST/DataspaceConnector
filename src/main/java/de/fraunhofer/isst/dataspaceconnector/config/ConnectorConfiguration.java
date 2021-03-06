package de.fraunhofer.isst.dataspaceconnector.config;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * This class handles policy settings: negotiation, pattern support, and usage control framework.
 */
@Data
@Configuration
@RequiredArgsConstructor
public class ConnectorConfiguration {
    /**
     * The clearing house access url.
     */
    @Value("${clearing.house.url}")
    private final @NonNull String clearingHouse;

    /**
     * The policy negotiation status from application.properties.
     */
    @Value("${policy.negotiation}")
    private @NonNull boolean policyNegotiation;

    /**
     * Setting for allowing unsupported patterns from application.properties.
     */
    @Value("${policy.allow-unsupported-patterns}")
    private @NonNull boolean allowUnsupported;

    /**
     * Usage control framework from application.properties.
     */
    @Value("${policy.framework}")
    private final @NonNull UsageControlFramework ucFramework;
}
