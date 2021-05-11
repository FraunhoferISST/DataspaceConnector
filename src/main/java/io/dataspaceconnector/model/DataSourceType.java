package io.dataspaceconnector.model;

public enum DataSourceType {

    DATABASE("Database"),
    REST("REST"),
    DIRECTORY("Directory"),
    FILE("File");

    private final String value;

    DataSourceType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
