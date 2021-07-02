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
package io.configmanager.extensions.routes.petrinet.evaluation.formula.transition;

import io.configmanager.extensions.routes.petrinet.evaluation.formula.state.StateFormula;
import io.configmanager.extensions.routes.petrinet.model.Arc;
import io.configmanager.extensions.routes.petrinet.model.Node;
import io.configmanager.extensions.routes.petrinet.model.Transition;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Evaluates to true, if parameter1 evaluates to true for every following transition
 * and parameter2 evaluates to true for every Place in between.
 */
@AllArgsConstructor
public class TransitionFORALL_MODAL implements TransitionFormula {
    private TransitionFormula parameter1;
    private StateFormula parameter2;

    public static TransitionFORALL_MODAL transitionFORALL_MODAL(final TransitionFormula parameter1,
                                                                final StateFormula parameter2) {
        return new TransitionFORALL_MODAL(parameter1, parameter2);
    }

    // parameter1, must be true for all successor transitions, parameter2 must
    // be true for the states between the current transition and its successors.
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        if (!(node instanceof Transition)) {
            return false;
        }

        final var followingPlaces = node.getSourceArcs().stream()
                .map(Arc::getTarget)
                .collect(Collectors.toSet());

        followingPlaces.retainAll(paths.stream().filter(path -> paths.size() == 2)
                .map(path -> path.get(0))
                .collect(Collectors.toSet()));

        final var followingTransitions = paths.stream().filter(path -> path.size() == 2)
                .filter(path -> followingPlaces.contains(path.get(0)))
                .map(path -> path.get(1))
                .collect(Collectors.toSet());

        for (final var transition : followingTransitions) {
            if (!parameter1.evaluate(transition, paths)) {
                return false;
            }
        }

        for (final var place : followingPlaces) {
            if (!parameter2.evaluate(place, paths)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String symbol() {
        return "FORALL_MODAL";
    }

    @Override
    public String writeFormula() {
        return String.format("%s(%s, %s)",
                symbol(),
                parameter1.writeFormula(),
                parameter2.writeFormula());
    }
}
