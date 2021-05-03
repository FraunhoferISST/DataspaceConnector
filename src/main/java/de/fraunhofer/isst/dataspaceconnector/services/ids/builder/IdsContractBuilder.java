package de.fraunhofer.isst.dataspaceconnector.services.ids.builder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractOfferBuilder;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Prohibition;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import de.fraunhofer.isst.ids.framework.util.IDSUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Converts DSC contracts to ids contract offers.
 */
@Component
@RequiredArgsConstructor
public final class IdsContractBuilder extends AbstractIdsBuilder<Contract, ContractOffer> {

    /**
     * The builder for ids permission.
     */
    private final @NonNull IdsPermissionBuilder permBuilder;

    /**
     * The builder for ids prohibition.
     */
    private final @NonNull IdsProhibitionBuilder prohBuilder;

    /**
     * The builder for ids duty.
     */
    private final @NonNull IdsDutyBuilder dutyBuilder;

    /**
     * The service for deserializing strings to ids rules.
     */
    private final @NonNull DeserializationService deserializer;

    @Override
    protected ContractOffer createInternal(final Contract contract, final URI baseUri,
                                           final int currentDepth, final int maxDepth)
            throws ConstraintViolationException {
        // Build children.
        final var permissions =
                create(permBuilder, onlyPermissions(contract.getRules()), baseUri,
                        currentDepth, maxDepth);
        final var prohibitions =
                create(prohBuilder, onlyProhibitions(contract.getRules()), baseUri,
                        currentDepth, maxDepth);
        final var duties =
                create(dutyBuilder, onlyDuties(contract.getRules()), baseUri, currentDepth,
                        maxDepth);

        // Prepare contract attributes.
        final var start = IdsUtils.getGregorianOf(contract.getStart());
        final var end = IdsUtils.getGregorianOf(contract.getEnd());
        final var consumer = contract.getConsumer();
        final var provider = contract.getProvider();

        final var builder = new ContractOfferBuilder(getAbsoluteSelfLink(contract, baseUri))
                ._contractStart_(start)
                ._contractEnd_(end)
                ._contractDate_(IDSUtils.getGregorianNow())
                ._consumer_(consumer)
                ._provider_(provider);

        permissions.ifPresent(builder::_permission_);
        prohibitions.ifPresent(builder::_prohibition_);
        duties.ifPresent(builder::_obligation_);

        return builder.build();
    }

    private List<ContractRule> onlyPermissions(final List<ContractRule> rules) {
        return Utils.toStream(rules).filter(this::isPermission).collect(Collectors.toList());
    }

    private List<ContractRule> onlyProhibitions(final List<ContractRule> rules) {
        return Utils.toStream(rules).filter(this::isProhibition).collect(Collectors.toList());
    }

    private List<ContractRule> onlyDuties(final List<ContractRule> rules) {
        return Utils.toStream(rules).filter(this::isDuty).collect(Collectors.toList());
    }

    private boolean isPermission(final ContractRule rule) {
        return deserializer.isRuleType(rule.getValue(), Permission.class);
    }

    private boolean isProhibition(final ContractRule rule) {
        return deserializer.isRuleType(rule.getValue(), Prohibition.class);
    }

    private boolean isDuty(final ContractRule rule) {
        return deserializer.isRuleType(rule.getValue(), Duty.class);
    }
}
