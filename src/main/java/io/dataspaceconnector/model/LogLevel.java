package io.dataspaceconnector.model;

public enum LogLevel {

    DEBUG_LEVEL_LOGGING("Debug Level Logging"),
    MINIMAL_LOGGING("Minimal Logging"),
    NO_LOGGING("No Logging");

    private final String value;

    LogLevel(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
