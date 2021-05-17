package io.dataspaceconnector.model;

/**
 * Enumeration for log level.
 */
public enum LogLevel {

    /**
     * Log level is debugging.
     */
    DEBUG_LEVEL_LOGGING("Debug Level Logging"),
    /**
     * Log level is minimal.
     */
    MINIMAL_LOGGING("Minimal Logging"),
    /**
     * No logging is used.
     */
    NO_LOGGING("No Logging");

    /**
     * The value of the enum.
     */
    private final String value;

    /**
     * @param value The value of the enum.
     */
    LogLevel(String value) {
        this.value = value;
    }

    /**
     * @return Enum value.
     */
    @Override
    public String toString() {
        return value;
    }
}
