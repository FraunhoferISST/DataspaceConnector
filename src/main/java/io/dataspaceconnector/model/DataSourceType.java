package io.dataspaceconnector.model;

/**
 * Enumeration for data source type
 */
public enum DataSourceType {

    /**
     * The database.
     */
    DATABASE("Database"),
    /**
     * REST as data source type.
     */
    REST("REST"),
    /**
     * Directory as data source type.
     */
    DIRECTORY("Directory"),

    /**
     * File as data source type.
     */
    FILE("File");

    /**
     * The value of the enum.
     */
    private final String value;

    /**
     * @param value The value of the enum.
     */
    DataSourceType(String value) {
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
