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
package io.dataspaceconnector.extension.petrinet.builder;

import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.common.ids.policy.RuleUtils;
import io.dataspaceconnector.extension.petrinet.evaluation.formula.Formula;
import io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionFormula;
import io.dataspaceconnector.common.ids.policy.PolicyPattern;

import java.net.URI;

import static io.dataspaceconnector.extension.petrinet.evaluation.formula.TrueOperator.trueOperator;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeMODAL.nodeMODAL;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeNOT.nodeNOT;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.ArcExpression.arcExpression;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionAF.transitionAF;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionAND.transitionAND;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionEV.transitionEV;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionNOT.transitionNOT;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionPOS.transitionPOS;

/**
 * For a given PolicyPattern, Rule and Resource ID (URI), create a Formula.
 */
public final class RuleFormulaBuilder {
    private RuleFormulaBuilder() {
        throw new UnsupportedOperationException();
    }

    /**
     * Builds a formula for a given PolicyPattern, Rule and Resource ID (URI).
     * @param pattern         The recognized policy pattern.
     * @param rule            The ids rule.
     * @param target          The requested/accessed element.
     * @return The build formula.
     */
    public static Formula buildFormula(final PolicyPattern pattern,
                                       final Rule rule,
                                       final URI target) {
        switch (pattern) {
            case PROVIDE_ACCESS:
                //when access is provided, policy is Fulfilled everytime
                return trueOperator();
            case USAGE_UNTIL_DELETION:
                return buildUsageUntilDeletionFormula(target);
            case USAGE_LOGGING:
                return buildLoggingFormula(target);
            case N_TIMES_USAGE:
                return buildNTimesUsageFormula(rule, target);
            case USAGE_NOTIFICATION:
                return buildNotificationFormula(target);
            case CONNECTOR_RESTRICTED_USAGE:
                return buildConnectorRestrictionFormula(rule, target);
            case PROHIBIT_ACCESS:
                return buildProhibitAccessFormula(target);
            default:
                //other rules are ignored
                return null;
        }
    }

    /**
     * @param rule the Policy Rule
     * @param target resource which is only allowed to be read n times
     * @return {@link Formula} describing the given rule
     */
    static Formula buildNTimesUsageFormula(final Rule rule, final URI target) {
        //in every possible path, resource is only allowed to be read maxUsage times
        final var maxUsage = RuleUtils.getMaxAccess(rule);
        TransitionFormula formula = transitionPOS(
                transitionAF(
                        arcExpression(
                                trans -> trans.getContext().getRead()
                                        .contains(target.toString()), "")));

        for (int i = 0; i < maxUsage; i++) {
            formula = transitionPOS(transitionAND(
                    transitionAF(arcExpression(
                            trans -> trans.getContext().getRead()
                                    .contains(target.toString()), "")), formula));
        }

        return nodeNOT(nodeMODAL(formula));
    }

    /**
     * @param target resource which has to be deleted after usage
     * @return {@link Formula} describing the given rule
     */
    static Formula buildUsageUntilDeletionFormula(final URI target) {
        //data has to be deleted after a reading transition but before the final node
        return nodeNOT(
                nodeMODAL(
                        transitionPOS(
                                transitionAND(
                                        transitionAF(
                                                arcExpression(
                                                        x -> x.getContext()
                                                                .getRead() != null
                                                && x.getContext().getRead()
                                                                .contains(target.toString()),
                                                        String
                                                                .format("Check if Transition"
                                                                        + " reads %s",
                                                                        target.toString()))),
                                        transitionNOT(
                                                transitionEV(
                                                        transitionAF(
                                                                arcExpression(
                                                                        x -> x.getContext()
                                                                                .getErase() != null
                                                                                && x.getContext()
                                                                                .getErase()
                                                                                .contains(
                                                                                    target
                                                                                    .toString()),
                        String.format("Check if Transition erases %s", target.toString()))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    /**
     * @param rule the policy rule
     * @param target resource which is only allowed to be read by targetconnector
     * @return {@link Formula} describing the given rule
     */
    static Formula buildConnectorRestrictionFormula(final Rule rule, final URI target) {
        //if a transition is reading the resource, it has to be from the allowedConnector
        final var allowedConnector = RuleUtils.getEndpoint(rule);
        return nodeNOT(
                nodeMODAL(
                        transitionPOS(
                                transitionAF(
                                        arcExpression(
                                                trans -> trans.getContext()
                                                        .getRead().contains(target.toString())
                                                        && !trans.getContext().getContext()
                                                        .contains(allowedConnector),
                                                String.format("IF transition reads "
                                                        + "resource %s it has to be from Connector "
                                                        + "%s", target.toString(), allowedConnector)
                                        )
                                )
                        )
                )
        );
    }

    /**
     * @param target resource for which access is forbidden
     * @return {@link Formula} describing the given rule
     */
    static Formula buildProhibitAccessFormula(final URI target) {
        //no reachable transition reads forbidden resource
        return nodeNOT(
                nodeMODAL(
                        transitionPOS(
                                transitionAF(
                                        arcExpression(
                                                trans -> trans.getContext()
                                                        .getRead().contains(target.toString()),
                                                String.format("Check IF transition "
                                                        + "tries to read %s", target.toString())
                                        )
                                )
                        )
                )
        );
    }

    /**
     * @param target resource for which reads must be logged
     * @return {@link Formula} describing the given rule
     */
    static Formula buildLoggingFormula(final URI target) {
        //every transition reading the resource has to contain a logging flag in context
        return nodeNOT(
                nodeMODAL(
                        transitionPOS(
                                transitionAF(
                                        arcExpression(trans -> trans
                                                        .getContext().getRead()
                                                        .contains(target.toString())
                                                        && !trans.getContext().getContext()
                                                            .contains("logging"),
                                                String.format("Check IF transition tries "
                                                        + "to read %s without set logging flag",
                                                        target.toString())
                                        )
                                )
                        )
                )
        );
    }

    /**
     * @param target resource for which the policy holds
     * @return {@link Formula} describing the given rule
     */
    static Formula buildNotificationFormula(final URI target) {
        //every transition reading the resource has to contain a notification flag in context
        return nodeNOT(
                nodeMODAL(
                        transitionPOS(
                                transitionAF(
                                        arcExpression(
                                                trans -> trans.getContext()
                                                        .getRead().contains(target.toString())
                                                        && !trans.getContext()
                                                            .getContext().contains("notification"),
                                                String.format("Check IF transition tries "
                                                        + "to read %s without set notification "
                                                        + "flag", target.toString())
                                        )
                                )
                        )
                )
        );
    }
}
