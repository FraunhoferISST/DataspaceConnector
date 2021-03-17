package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

public enum ErrorMessages {
    DESC_NULL("The description parameter may not be null."),
    ENTITYID_NULL("The entity id may not be null."),
    PAGEABLE_NULL("The pageable parameter may not be null"),
    ENTITYSET_NULL("The set of entities may not be null");


    private final String value;

    ErrorMessages(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
