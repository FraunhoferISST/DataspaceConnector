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
import io.dataspaceconnector.extension.petrinet.model.Node;
import lombok.AllArgsConstructor;

import java.util.List;

import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeMODAL.nodeMODAL;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionAND.transitionAND;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionMODAL.transitionMODAL;

/**
 * Evaluates to true, if there is a successor transition for which parameter1 holds,
 * while parameter2 holds for the place in between.
 */
@AllArgsConstructor
public class TransitionEXISTMODAL implements TransitionFormula {
    /**
     * Successor transition needs to hold for this formula.
     */
    private TransitionFormula parameter1;

    /**
     * Place in between transition needs to hold for this formula.
     */
    private StateFormula parameter2;

    /**
     * Evaluates to true, if there is a successor transition for which parameter1 holds,
     * while parameter2 holds for the place in between.
     * @param formula1 Successor transition needs to hold for this formula.
     * @param formula2 Place in between transition needs to hold for this formula.
     * @return Transition representing the formula.
     */
    public static TransitionEXISTMODAL transitionEXISTMODAL(final TransitionFormula formula1,
                                                            final StateFormula formula2) {
        return new TransitionEXISTMODAL(formula1, formula2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return nodeMODAL(transitionAND(parameter1,
                transitionMODAL(parameter2))).evaluate(node, paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String symbol() {
        return "EXIST_MODAL";
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
