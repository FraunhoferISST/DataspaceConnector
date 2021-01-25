package de.fraunhofer.isst.dataspaceconnector.model.v2;

public interface BaseFactory<T extends BaseResource,
        D extends BaseDescription<T>> {
    T create(D desc);

    boolean update(T type, D desc);
}
