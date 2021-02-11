package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.model.BaseResource;

public interface BaseViewer<T extends BaseResource, V extends BaseView<T>> {
    V create(T view);
}
