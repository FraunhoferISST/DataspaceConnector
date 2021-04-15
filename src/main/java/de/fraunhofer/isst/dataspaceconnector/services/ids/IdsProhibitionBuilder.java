package de.fraunhofer.isst.dataspaceconnector.services.ids;

import de.fraunhofer.iais.eis.Prohibition;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Converts DSC Rule to Infomodel Prohibition rule.
 */
@Component
public class IdsProhibitionBuilder extends IdsRuleBuilder<Prohibition> {
    IdsProhibitionBuilder(@NonNull final DeserializationService deserializer) {
        super(deserializer, Prohibition.class);
    }
}
