package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

public enum Basepaths {
    Resources("https://localhost:8080/api/v2/resources"),
    Representations("https://localhost:8080/api/v2/representations"),
    Contracts("https://localhost:8080/api/v2/contracts"),
    Artifacts("https://localhost:8080/api/v2/artifacts"),
    Rules("https://localhost:8080/api/v2/rules");

    public final String label;

    private Basepaths(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
