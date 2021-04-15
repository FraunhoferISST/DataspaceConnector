package de.fraunhofer.isst.dataspaceconnector.services.ids;

import de.fraunhofer.iais.eis.Permission;
import lombok.NonNull;

class IdsPermissionBuilder extends IdsRuleBuilder<Permission> {
    IdsPermissionBuilder(@NonNull final DeserializationService deserializer) {
        super(deserializer, Permission.class);
    }
}
