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
import io.dataspaceconnector.extension.petrinet.model.Transition;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Evaluates to true, if given {@link ArcExpression} evaluates to true.
 */
@AllArgsConstructor
public class TransitionAF implements TransitionFormula {
    /**
     * Expression which needs to evaluate to true.
     */
    private ArcExpression parameter;

    /**
     * Transition evaluates to true, if given {@link ArcExpression} evaluates to true.
     * @param parameter The expression.
     * @return The build transition.
     */
    public static TransitionAF transitionAF(final ArcExpression parameter) {
        return new TransitionAF(parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return node instanceof Transition
                && parameter.getSubExpression().evaluate((Transition) node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String symbol() {
        return "AF";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.getMessage());
    }
}
