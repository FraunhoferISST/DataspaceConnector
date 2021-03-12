package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;

public enum BasePath {
    RESOURCES("/api/resources"),
    REPRESENTATIONS("/api/representations"),
    CONTRACTS("/api/contracts"),
    ARTIFACTS("/api/artifacts"),
    RULES("/api/rules"),
    CATALOGS("/api/catalogs");

    public final String label;

    private BasePath(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        final var host = EndpointUtils.getCurrentBasePathString();
        return host + "/" + label;
    }
}
