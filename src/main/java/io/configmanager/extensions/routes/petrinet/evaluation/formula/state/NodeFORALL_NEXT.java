/*
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
package io.configmanager.extensions.routes.petrinet.evaluation.formula.state;

import io.configmanager.extensions.routes.petrinet.model.Node;
import lombok.AllArgsConstructor;

import java.util.List;

import static io.configmanager.extensions.routes.petrinet.evaluation.formula.state.NodeEXIST_NEXT.nodeEXIST_NEXT;
import static io.configmanager.extensions.routes.petrinet.evaluation.formula.state.NodeNOT.nodeNOT;

/**
 * Evaluates to true, if all following places satisfy the given formula.
 */
@AllArgsConstructor
public class NodeFORALL_NEXT implements StateFormula {
    private StateFormula parameter;

    public static NodeFORALL_NEXT nodeFORALL_NEXT(final StateFormula parameter) {
        return new NodeFORALL_NEXT(parameter);
    }

    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return nodeNOT(nodeEXIST_NEXT(nodeNOT(parameter))).evaluate(node, paths);
    }

    @Override
    public String symbol() {
        return "FORALL_NEXT";
    }

    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.writeFormula());
    }

}
