package io.dataspaceconnector.model.base;

import java.net.URI;

public interface RemoteService extends Remote {
    URI getLocation();
}
