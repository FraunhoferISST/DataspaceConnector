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
package io.dataspaceconnector.extension.petrinet.evaluation.formula;

import io.dataspaceconnector.extension.petrinet.model.Node;

import java.util.List;

/**
 * A generic Formula, can be a StateFormula or a TransitionFormula.
 */
public interface Formula {
    /**
     * Evaluates a given formula.
     * @param node The starting node.
     * @param paths The node path.
     * @return Evaluation result.
     */
    boolean evaluate(Node node, List<List<Node>> paths);

    /**
     * The representing symbol of the formula.
     * @return The symbol representing the formula.
     */
    String symbol();

    /**
     * Converts the formula into a String.
     * @return The formula as String.
     */
    String writeFormula();
}
