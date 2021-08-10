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

import io.dataspaceconnector.extension.petrinet.evaluation.formula.state.StateFormula;
import io.dataspaceconnector.extension.petrinet.model.Arc;
import io.dataspaceconnector.extension.petrinet.model.Node;
import io.dataspaceconnector.extension.petrinet.model.Transition;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Evaluates to true, if parameter evaluates to true for a place directly following the transition.
 */
@AllArgsConstructor
public class TransitionMODAL implements TransitionFormula {
    /**
     * Parameter needs to evaluate to true for a place directly following the transition.
     */
    private StateFormula parameter;

    /**
     * Evaluates to true, if parameter evaluates to true for a place directly
     * following the transition.
     * @param parameter Parameter needs to evaluate to true for a
     *                  place directly following the transition.
     * @return Transition representing the formula.
     */
    public static TransitionMODAL transitionMODAL(final StateFormula parameter) {
        return new TransitionMODAL(parameter);
    }

    // MODAL, is true if parameter evaluates to true for a state following the current transition
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return node instanceof Transition
                && node.getSourceArcs().stream()
                        .map(Arc::getTarget)
                        .map(place -> parameter.evaluate(place, paths))
                        .reduce(false, (a, b) -> a || b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String symbol() {
        return "MODAL";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.writeFormula());
    }

}
