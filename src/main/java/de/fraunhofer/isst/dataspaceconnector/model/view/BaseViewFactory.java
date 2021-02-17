package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.model.BaseEntity;

public interface BaseViewFactory<T extends BaseEntity, V extends BaseView<T>> {
    V create(T view);
}
