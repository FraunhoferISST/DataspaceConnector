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
package io.dataspaceconnector.extension.petrinet.evaluation.formula.state;

import io.dataspaceconnector.extension.petrinet.model.Node;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Evaluates to true, if at least one of the two subformulas evaluates to true.
 */
@AllArgsConstructor
public class NodeOR implements StateFormula {
    /**
     * Subformula 1.
     */
    private StateFormula parameter1;

    /**
     * Subformula 2.
     */
    private StateFormula parameter2;

    /**
     * Evaluates to true, if at least one of the two subformulas evaluates to true.
     * @param parameter1 Subformula 1.
     * @param parameter2 Subformula 2.
     * @return Node representing the formula.
     */
    public static NodeOR nodeOR(final StateFormula parameter1,
                                final StateFormula parameter2) {
        return new NodeOR(parameter1, parameter2);
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
