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

/**
 * Evaluates to true, if at least one of the two subformulas evaluates to true.
 */
@AllArgsConstructor
public class TransitionOR implements TransitionFormula {
    /**
     * One of two subformula-candidates to evanulate to true.
     */
    private TransitionFormula parameter1;

    /**
     * One of two subformula-candidates to evanulate to true.
     */
    private TransitionFormula parameter2;

    /**
     * Evaluates to true, if at least one of the two subformulas evaluates to true.
     * @param parameter1 One of two subformula-candidates to evanulate to true.
     * @param parameter2 One of two subformula-candidates to evanulate to true.
     * @return Transition representing the formula.
     */
    public static TransitionOR transitionOR(final TransitionFormula parameter1,
                                            final TransitionFormula parameter2) {
        return new TransitionOR(parameter1, parameter2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return parameter1.evaluate(node, paths) || parameter2.evaluate(node, paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String symbol() {
        return "OR";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeFormula() {
        return String.format("%s(%s, %s)",
                symbol(),
                parameter1.writeFormula(),
                parameter2.writeFormula());
    }
}
