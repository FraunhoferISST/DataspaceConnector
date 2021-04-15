package de.fraunhofer.isst.dataspaceconnector.services.ids;

import de.fraunhofer.iais.eis.Permission;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Converts DSC Rule to Infomodel Permission rule.
 */
@Component
public class IdsPermissionBuilder extends IdsRuleBuilder<Permission> {
    IdsPermissionBuilder(@NonNull final DeserializationService deserializer) {
        super(deserializer, Permission.class);
    }
}
