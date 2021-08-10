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

import static io.dataspaceconnector.extension.petrinet.evaluation.formula.TrueOperator.trueOperator;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeEXISTUNTIL.nodeEXISTUNTIL;

/**
 * Evaluates to true, if some Place is reachable, which fulfills the given parameter.
 */
@AllArgsConstructor
public class NodePOS implements StateFormula {
    /**
     * Formula which must be fulfilled at some reachable place.
     */
    private StateFormula parameter;

    /**
     * Formula which evaluates to true, if some Place is reachable,
     * which fulfills the given parameter.
     * @param parameter Formula which must be fulfilled at some reachable place.
     * @return Node representing the formula.
     */
    public static NodePOS nodePOS(final StateFormula parameter) {
        return new NodePOS(parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return nodeEXISTUNTIL(trueOperator(), parameter).evaluate(node, paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String symbol() {
        return "POS";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.writeFormula());
    }
}
