package io.dataspaceconnector.services.ids.builder;

import de.fraunhofer.iais.eis.Prohibition;
import io.dataspaceconnector.services.ids.DeserializationService;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Converts DSC rule to ids prohibition.
 */
@Component
public class IdsProhibitionBuilder extends IdsRuleBuilder<Prohibition> {
    IdsProhibitionBuilder(@NonNull final DeserializationService deserializer) {
        super(deserializer, Prohibition.class);
    }
}
