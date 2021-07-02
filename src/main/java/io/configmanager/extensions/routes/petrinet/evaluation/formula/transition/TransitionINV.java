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

import io.configmanager.extensions.routes.petrinet.model.Node;
import lombok.AllArgsConstructor;

import java.util.List;

import static io.configmanager.extensions.routes.petrinet.evaluation.formula.transition.TransitionNOT.transitionNOT;
import static io.configmanager.extensions.routes.petrinet.evaluation.formula.transition.TransitionPOS.transitionPOS;

/**
 * Evaluates to true, if parameter evaluates to true for all reachable transitions.
 */
@AllArgsConstructor
public class TransitionINV implements TransitionFormula {
    private TransitionFormula parameter;

    public static TransitionINV transitionINV(final TransitionFormula parameter) {
        return new TransitionINV(parameter);
    }

    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return transitionNOT(transitionPOS(transitionNOT(parameter))).evaluate(node, paths);
    }

    @Override
    public String symbol() {
        return "INV";
    }

    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.writeFormula());
    }
}
