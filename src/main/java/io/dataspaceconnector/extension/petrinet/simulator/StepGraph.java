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

import io.dataspaceconnector.extension.petrinet.model.PetriNet;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Graph containing every Step a Petri Net can make in its execution.
 */
@Getter
public class StepGraph {
    /**
     * The inital PetriNet.
     */
    private PetriNet initial;

    /**
     * Each Step a PetriNet can make is represented as a PetriNet.
     */
    private Set<PetriNet> steps;

    /**
     * Arc which Steps are reachable from given Steps.
     */
    private Set<NetArc> arcs;

    /**
     * Creates a Step-Graph of the given Petrinet, containing every Step a PetriNet can make.
     * @param pInitial The initial Petrinet.
     */
    public StepGraph(final PetriNet pInitial) {
        this.initial = pInitial;
        steps = new HashSet<>();
        arcs = new HashSet<>();
    }
}
