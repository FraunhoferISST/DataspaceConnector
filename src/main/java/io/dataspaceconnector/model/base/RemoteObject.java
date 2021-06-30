package io.dataspaceconnector.model.base;

import java.net.URI;

public interface RemoteObject extends Remote {
    URI getRemoteId();
}
