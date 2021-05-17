package io.dataspaceconnector.model;

/**
 * Enumeration for registration status.
 */
public enum RegisterStatus {

    /**
     * Registered status.
     */
    REGISTERED("Registered"),
    /**
     * Unregistered status.
     */
    UNREGISTERED("Unregistered");

    /**
     * The value of the enum.
     */
    private final String value;

    /**
     * @param value The value of the enum.
     */
    RegisterStatus(String value) {
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
