package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;

public enum Basepaths {
    Resources("/api/v2/resources"),
    Representations("/api/v2/representations"),
    Contracts("/api/v2/contracts"),
    Artifacts("/api/v2/artifacts"),
    Rules("/api/v2/rules");

    public final String label;

    private Basepaths(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        final var host = EndpointUtils.getCurrentBasePathString();
        return host + "/" + label;
    }
}
