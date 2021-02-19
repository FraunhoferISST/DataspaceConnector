package de.fraunhofer.isst.dataspaceconnector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * This class handles policy settings: negotiation, pattern support, and usage control framework.
 */
@Configuration
public class PolicyConfiguration {
    /**
     * The clearing house access url.
     */
    @Value("${clearing.house.url}")
    private String clearingHouse;

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
    private static UsageControlFramework ucFramework;

    /**
     * Return policy negotiation status.
     *
     * @return True if on, false if off.
     */
    public boolean isPolicyNegotiation() {
        return policyNegotiation;
    }

    /**
     * Set negotiation status.
     *
     * @param policyNegotiation The policy negotiation status.
     */
    @SuppressWarnings("checkstyle:HiddenField")
    public void setPolicyNegotiation(final boolean policyNegotiation) {
        this.policyNegotiation = policyNegotiation;
    }

    /**
     * Return if unsupported pattern are supported.
     *
     * @return True if yes, false if no.
     */
    public boolean isAllowUnsupported() {
        return allowUnsupported;
    }

    /**
     * Allow or prohibit unsupported patterns.
     *
     * @param allowUnsupported The unsupported pattern boolean.
     */
    @SuppressWarnings("checkstyle:HiddenField")
    public void setAllowUnsupported(final boolean allowUnsupported) {
        this.allowUnsupported = allowUnsupported;
    }

    /**
     * Return usage control framework.
     *
     * @return The usage control framework enum.
     */
    public UsageControlFramework getUcFramework() {
        return ucFramework;
    }

    /**
     * This class provides a usage control framework enum.
     */
    public enum UsageControlFramework {
        /**
         * Usage control (enforcement) inside the connector.
         */
        INTERNAL("INTERNAL"),
        /**
         * Usage control framework MyData.
         */
        MY_DATA("MY_DATA");

        /**
         * The usage control framework.
         */
        private final String framework;

        UsageControlFramework(final String string) {
            framework = string;
        }

        @Override
        public String toString() {
            return framework;
        }
    }
}
