package de.fraunhofer.isst.dataspaceconnector.config;

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
