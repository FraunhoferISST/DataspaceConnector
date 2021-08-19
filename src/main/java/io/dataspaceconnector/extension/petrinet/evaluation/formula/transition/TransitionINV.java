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
package io.dataspaceconnector.extension.petrinet.evaluation.formula.transition;

import io.dataspaceconnector.extension.petrinet.model.Node;
import lombok.AllArgsConstructor;

import java.util.List;

import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionNOT.transitionNOT;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionPOS.transitionPOS;

/**
 * Evaluates to true, if parameter evaluates to true for all reachable transitions.
 */
@AllArgsConstructor
public class TransitionINV implements TransitionFormula {
    /**
     * Parameter needs to evaluate to true for all reachable transitions.
     */
    private TransitionFormula parameter;

    /**
     * Evaluates to true, if parameter evaluates to true for all reachable transitions.
     * @param formula Parameter needs to evaluate to true for all reachable transitions.
     * @return Transition representing the formula.
     */
    public static TransitionINV transitionINV(final TransitionFormula formula) {
        return new TransitionINV(formula);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return transitionNOT(transitionPOS(transitionNOT(parameter))).evaluate(node, paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String symbol() {
        return "INV";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.writeFormula());
    }
}
