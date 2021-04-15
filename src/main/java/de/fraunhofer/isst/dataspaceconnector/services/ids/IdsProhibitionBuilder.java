package de.fraunhofer.isst.dataspaceconnector.services.ids;

import de.fraunhofer.iais.eis.Prohibition;
import lombok.NonNull;

class IdsProhibitionBuilder extends IdsRuleBuilder<Prohibition> {
    IdsProhibitionBuilder(@NonNull final DeserializationService deserializer) {
        super(deserializer, Prohibition.class);
    }
}
