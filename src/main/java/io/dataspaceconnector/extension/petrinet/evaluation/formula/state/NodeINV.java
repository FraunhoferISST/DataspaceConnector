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

import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeNOT.nodeNOT;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodePOS.nodePOS;
/**
 * Evaluates to true, if parameter evaluates to true for all reachable places.
 */
@AllArgsConstructor
public class NodeINV implements StateFormula {
    /**
     * Formula must evaluate to true for every reachable place.
     */
    private StateFormula parameter;

    /**
     * Evaluates to true, if parameter evaluates to true for all reachable places.
     * @param parameter Formula which needs to hold for every reachable place.
     * @return The node representing the formula.
     */
    public static NodeINV nodeINV(final StateFormula parameter) {
        return new NodeINV(parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return nodeNOT(nodePOS(nodeNOT(parameter))).evaluate(node, paths);
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
