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

import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeEV.nodeEV;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeNOT.nodeNOT;

/**
 * Evaluates to true, if there exists a path, where parameter holds for every place.
 */
@AllArgsConstructor
public class NodeALONG implements StateFormula {
    /**
     * Parameter needed to hold for every place.
     */
    private StateFormula parameter;

    /**
     * Evaluates to true, if there exists a path, where parameter holds for every place.
     * @param parameter Formula which needs to hold for every place.
     * @return The node representing the formula.
     */
    public static NodeALONG nodeALONG(final StateFormula parameter) {
        return new NodeALONG(parameter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return nodeNOT(nodeEV(nodeNOT(parameter))).evaluate(node, paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String symbol() {
        return "ALONG";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.writeFormula());
    }
}
