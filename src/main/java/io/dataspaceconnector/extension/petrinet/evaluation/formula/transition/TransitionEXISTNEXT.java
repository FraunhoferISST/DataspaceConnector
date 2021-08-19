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

import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeMODAL.nodeMODAL;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionMODAL.transitionMODAL;

/**
 * Evaluates to true, if there is a following transition fulfilling the given formula.
 */
@AllArgsConstructor
public class TransitionEXISTNEXT implements TransitionFormula {
    /**
     * Following transition needs to fulfill this formula.
     */
    private TransitionFormula parameter;

    /**
     * Evaluates to true, if there is a following transition fulfilling the given formula.
     * @param parameter Following transition needs to fulfill this formula.
     * @return Transition representing the formula.
     */
    public static TransitionEXISTNEXT transitionEXISTNEXT(final TransitionFormula parameter) {
        return new TransitionEXISTNEXT(parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return transitionMODAL(nodeMODAL(parameter)).evaluate(node, paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String symbol() {
        return "EXIST_NEXT";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.writeFormula());
    }
}
