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
package io.dataspaceconnector.extension.petrinet.simulator;

import io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.ArcSubExpression;
import io.dataspaceconnector.extension.petrinet.model.Transition;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Methods to check parallel evaluation of a
 * {@link io.dataspaceconnector.extension.petrinet.model.PetriNet}.
 */
public final class ParallelEvaluator {
    private ParallelEvaluator() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param condition a condition to be fulfilled by a transition
     * @param n number of transitions to fulfill the condition in parallel
     * @param parallelSets sets of parallel transitions
     *                     (previously calculated through stepgraph of unfolded petrinet)
     * @return true if at least n transitions fulfilling condition
     *         are parallely executed at some point
     */
    public static boolean nParallelTransitionsWithCondition(final ArcSubExpression condition,
                                                        final int n,
                                                        final List<List<Transition>> parallelSets) {
        final var setsWithSizeAtLeastN = parallelSets
                .stream()
                .filter(transitions -> transitions.size() >= n)
                .collect(Collectors.toSet());

        for (final var set: setsWithSizeAtLeastN) {
            if (set.stream().filter(condition::evaluate).count() >= n) {
                return true;
            }
        }
        return false;
    }
}
