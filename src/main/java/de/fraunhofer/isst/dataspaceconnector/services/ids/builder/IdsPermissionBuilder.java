package de.fraunhofer.isst.dataspaceconnector.services.ids.builder;

import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Converts DSC rule to ids permission.
 */
@Component
public class IdsPermissionBuilder extends IdsRuleBuilder<Permission> {
    IdsPermissionBuilder(@NonNull final DeserializationService deserializer) {
        super(deserializer, Permission.class);
    }
}
