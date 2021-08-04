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

import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionEXISTNEXT.transitionEXISTNEXT;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionNOT.transitionNOT;

/**
 * Evaluates to true, if all following transitions satisfy the given formula.
 */
@AllArgsConstructor
public class TransitionFORALLNEXT implements TransitionFormula {
    /**
     * All following transitions need to satisfy the given formula.
     */
    private TransitionFormula parameter;

    /**
     * Evaluates to true, if all following transitions satisfy the given formula.
     * @param formula All following transitions need to satisfy the given formula.
     * @return Transition representing the formula.
     */
    public static TransitionFORALLNEXT transitionFORALLNEXT(final TransitionFormula formula) {
        return new TransitionFORALLNEXT(formula);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return transitionNOT(transitionEXISTNEXT(transitionNOT(parameter))).evaluate(node, paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String symbol() {
        return "FORALL_NEXT";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.writeFormula());
    }
}
