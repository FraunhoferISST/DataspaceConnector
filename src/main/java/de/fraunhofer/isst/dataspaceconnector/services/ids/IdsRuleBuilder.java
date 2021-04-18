package de.fraunhofer.isst.dataspaceconnector.services.ids;

import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.net.URI;

/**
 * The base class for constructing an ids rule from a DSC rule.
 *
 * @param <T> The ids rule type.
 */
@RequiredArgsConstructor
public class IdsRuleBuilder<T extends Rule> extends AbstractIdsBuilder<ContractRule, T> {

    /**
     * The service for deserializing strings to ids rules.
     */
    private final @NonNull DeserializationService deserializer;

    /**
     * The type of the rule to be build. Needed for the deserializer.
     */
    private final @NonNull Class<T> ruleType;

    @Override
    protected final T createInternal(final ContractRule rule, final URI baseUri,
                                     final int currentDepth, final int maxDepth) {
        final var idsRule = deserializer.getRule(rule.getValue());
        final var selfLink = getAbsoluteSelfLink(rule, baseUri);
        var newRule = rule.getValue();
        if (idsRule.getId() == null) {
            // No id has been set for this rule. Thus, no references can be found.
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
