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

import io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionFormula;
import io.dataspaceconnector.extension.petrinet.model.Node;
import lombok.AllArgsConstructor;

import java.util.List;

import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeAND.nodeAND;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeMODAL.nodeMODAL;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionMODAL.transitionMODAL;

/**
 * Evaluates to true, if there is a successor place for which parameter1 holds,
 * while parameter2 holds for the transition in between.
 */
@AllArgsConstructor
public class NodeEXISTMODAL implements StateFormula {
    /**
     * A successor place.
     */
    private StateFormula parameter1;

    /**
     * The transition to the successor place.
     */
    private TransitionFormula parameter2;

    /**
     * Evaluates to true, if there is a successor place for which parameter1 holds,
     * while parameter2 holds for the transition in between.
     * @param parameter1 A successor place.
     * @param parameter2 The transition to the successor place.
     * @return Node representing the formula.
     */
    public static NodeEXISTMODAL nodeEXISTMODAL(final StateFormula parameter1,
                                                  final TransitionFormula parameter2) {
        return new NodeEXISTMODAL(parameter1, parameter2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return transitionMODAL(nodeAND(parameter1,
                nodeMODAL(parameter2))).evaluate(node, paths);
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
        return String.format("%s(%s, %s)", symbol(),
                parameter1.writeFormula(),
                parameter2.writeFormula());
    }
}
