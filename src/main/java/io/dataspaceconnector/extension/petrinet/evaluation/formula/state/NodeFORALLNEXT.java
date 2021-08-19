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

import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeEXISTNEXT.nodeEXISTNEXT;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeNOT.nodeNOT;

/**
 * Evaluates to true, if all following places satisfy the given formula.
 */
@AllArgsConstructor
public class NodeFORALLNEXT implements StateFormula {
    /**
     * Formula which all places must fulfill.
     */
    private StateFormula parameter;

    /**
     * Node which evaluates to true, if all following places satisfy the given formula.
     * @param parameter Formula which all places must fulfill.
     * @return Node representing the formula.
     */
    public static NodeFORALLNEXT nodeFORALLNEXT(final StateFormula parameter) {
        return new NodeFORALLNEXT(parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return nodeNOT(nodeEXISTNEXT(nodeNOT(parameter))).evaluate(node, paths);
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
