package de.fraunhofer.isst.dataspaceconnector.services.ids;

import java.net.URI;

import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class IdsRuleBuilder<T extends Rule> extends AbstractIdsBuilder<ContractRule, T> {

    private final @NonNull DeserializationService deserializer;

    private final @NonNull Class<T> ruleType;

    //    IdsRuleBuilder(@NonNull DeserializationService deserializationService) {
    //        this.deserializationService = deserializationService;
    //        final var resolved = GenericTypeResolver.resolveTypeArguments(getClass(),
    //        IdsRuleBuilder.class);
    //        ruleType = (Class<T>) resolved[1];
    //    }

    @Override
    protected T createInternal(final ContractRule rule, final URI baseUri, final int currentDepth,
                               final int maxDepth) {
        final var idsRule = deserializer.getRule(rule.getValue());
        final var selfLink = getAbsoluteSelfLink(rule, baseUri);
        var newRule = rule.getValue();
        if (idsRule.getId() == null) {
            // No id has been set for this rule.
            // Since no id has been set for this rule, no references can be found.
            // Inject the real id.
            newRule = newRule.substring(0, newRule.indexOf("{")) + "\"@id :\" " + newRule
                    .substring(newRule.indexOf("{"));
        } else {
            // The id has been set, there may be references.
            // Search for the id and replace everywhere.
            newRule = newRule.replace(idsRule.getId().toString(), selfLink.toString());

        }

        return deserializer.getRule(newRule, ruleType);
    }
}
