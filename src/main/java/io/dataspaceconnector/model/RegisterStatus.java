package io.dataspaceconnector.model;

public enum RegisterStatus {

    REGISTERED("Registered"),
    UNREGISTERED("Unregistered");

    private final String value;

    RegisterStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
