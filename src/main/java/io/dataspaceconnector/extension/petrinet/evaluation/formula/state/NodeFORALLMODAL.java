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
import io.dataspaceconnector.extension.petrinet.model.Arc;
import io.dataspaceconnector.extension.petrinet.model.Node;
import io.dataspaceconnector.extension.petrinet.model.Place;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Evaluates to true, if parameter1 evaluates to true for every following
 * place and parameter2 evaluates to true
 * for every transition in between.
 */
@AllArgsConstructor
public class NodeFORALLMODAL implements StateFormula {
    /**
     * Parameter which needs to evaluate to true for every folliwing place.
     */
    private StateFormula parameter1;

    /**
     * Parameter which needs to evaluate to true for every transition.
     */
    private TransitionFormula parameter2;


    /**
     * Evaluates to true, if parameter1 evaluates to true for every following
     * place and parameter2 evaluates to true
     * for every transition in between.
     * @param parameter1 Needs to evanluate to true for every following place.
     * @param parameter2 Needs to evaluate to true for every transition.
     * @return Node representing the formula.
     */
    public static NodeFORALLMODAL nodeFORALLMODAL(final StateFormula parameter1,
                                                  final TransitionFormula parameter2) {
        return new NodeFORALLMODAL(parameter1, parameter2);
    }

    /**
     * {@inheritDoc}
     */
    // parameter1, must be true for all successor states, parameter2 must
    // be true for the transitions between the current state and its successors.
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        if (!(node instanceof Place)) {
            return false;
        }

        final var followingTransitions = paths.stream()
                .filter(path -> path.size() == 2 && path.get(0) == node)
                .map(path -> path.get(1))
                .collect(Collectors.toSet());

        final var followingPlaces = followingTransitions.stream()
                .map(Node::getSourceArcs)
                .flatMap(Collection::stream)
                .map(Arc::getTarget)
                .collect(Collectors.toSet());

        for (final var place : followingPlaces) {
            if (!parameter1.evaluate(place, paths)) {
                return false;
            }
        }

        for (final var transition : followingTransitions) {
            if (!parameter2.evaluate(transition, paths)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String symbol() {
        return "FORALL_MODAL";
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
