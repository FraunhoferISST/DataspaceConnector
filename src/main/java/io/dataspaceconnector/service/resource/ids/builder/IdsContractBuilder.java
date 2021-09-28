/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.service.resource.ids.builder;

import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractOfferBuilder;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Prohibition;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.ids.mapping.ToIdsObjectMapper;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.service.resource.ids.builder.base.AbstractIdsBuilder;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts dsc contracts to ids contract offers.
 */
@Component
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

    /**
     * Constructs an IdsContractBuilder.
     *
     * @param selfLinkHelper the self link helper.
     * @param idsPermissionBuilder the permission builder.
     * @param idsProhibitionBuilder the prohibition builder.
     * @param idsDutyBuilder the duty builder.
     * @param deserializationService the deserialization service.
     */
    @Autowired
    public IdsContractBuilder(final SelfLinkHelper selfLinkHelper,
                              final IdsPermissionBuilder idsPermissionBuilder,
                              final IdsProhibitionBuilder idsProhibitionBuilder,
                              final IdsDutyBuilder idsDutyBuilder,
                              final DeserializationService deserializationService) {
        super(selfLinkHelper);
        this.permBuilder = idsPermissionBuilder;
        this.prohBuilder = idsProhibitionBuilder;
        this.dutyBuilder = idsDutyBuilder;
        this.deserializer = deserializationService;
    }

    @Override
    protected ContractOffer createInternal(final Contract contract, final int currentDepth,
                                           final int maxDepth)
            throws ConstraintViolationException {
        // Build children.
        final var permissions =
                create(permBuilder, onlyPermissions(contract.getRules()), currentDepth, maxDepth);
        final var prohibitions =
                create(prohBuilder, onlyProhibitions(contract.getRules()), currentDepth, maxDepth);
        final var duties =
                create(dutyBuilder, onlyDuties(contract.getRules()), currentDepth, maxDepth);

        // Build contract only if at least one rule is present.
        if (permissions.isEmpty() && prohibitions.isEmpty() && duties.isEmpty()) {
            return null;
        }

        boolean permissionsEmpty = false;
        boolean prohibitionsEmpty = false;
        boolean dutiesEmpty = false;

        if (permissions.isPresent()) {
            permissionsEmpty = permissions.get().isEmpty();
        }

        if (prohibitions.isPresent()) {
            prohibitionsEmpty = prohibitions.get().isEmpty();
        }

        if (duties.isPresent()) {
            dutiesEmpty = duties.get().isEmpty();
        }

        if (permissionsEmpty && prohibitionsEmpty && dutiesEmpty) {
            return null;
        }

        // Prepare contract attributes.
        final var start = ToIdsObjectMapper.getGregorianOf(contract.getStart());
        final var end = ToIdsObjectMapper.getGregorianOf(contract.getEnd());
        final var consumer = contract.getConsumer();
        final var provider = contract.getProvider();

        final var builder = new ContractOfferBuilder(getAbsoluteSelfLink(contract))
                ._contractStart_(start)
                ._contractEnd_(end)
                ._contractDate_(IdsMessageUtils.getGregorianNow())
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
