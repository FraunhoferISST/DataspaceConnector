package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseResource;

public interface BaseViewer<T extends BaseResource, V extends BaseView<T>> {
    V create(T view);
}
