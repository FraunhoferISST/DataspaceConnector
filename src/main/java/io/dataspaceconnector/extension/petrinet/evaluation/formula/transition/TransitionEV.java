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

import static io.dataspaceconnector.extension.petrinet.evaluation.formula.TrueOperator.trueOperator;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionFORALLUNTIL.transitionFORALLUNTIL;

/**
 * Evaluates to true, if a transition fulfilling the given parameter
 * is eventually reached on every path.
 */
@AllArgsConstructor
public class TransitionEV implements TransitionFormula {
    /**
     * The formula which needs to be fulfilled.
     */
    private TransitionFormula parameter;

    /**
     * Evaluates to true, if a transition fulfilling the given parameter
     * is eventually reached on every path.
     * @param parameter The formula which needs to be fulfilled.
     * @return Transition representing the formula.
     */
    public static TransitionEV transitionEV(final TransitionFormula parameter) {
        return new TransitionEV(parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return transitionFORALLUNTIL(trueOperator(), parameter).evaluate(node, paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String symbol() {
        return "EV";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.writeFormula());
    }
}
