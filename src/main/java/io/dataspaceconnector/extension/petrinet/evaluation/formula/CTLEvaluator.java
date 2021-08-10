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

import io.dataspaceconnector.extension.petrinet.evaluation.formula.state.StateFormula;
import io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionFormula;
import io.dataspaceconnector.extension.petrinet.model.Node;
import io.dataspaceconnector.extension.petrinet.model.Place;
import io.dataspaceconnector.extension.petrinet.model.Transition;

import java.util.List;

/**
 * Evaluate a {@link Formula} on a given {@link Node} for a set of Paths.
 */
public final class CTLEvaluator {
    private CTLEvaluator() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param ctlExpression a {@link StateFormula} to evaluate
     * @param place a {@link Place} of a
     *      {@link io.dataspaceconnector.extension.petrinet.model.PetriNet}
     * @param paths possible pathes through the PetriNet
     * @return result of the evaluation of the ctlExpression
     */
    public static boolean evaluateNode(final StateFormula ctlExpression,
                                       final Place place,
                                       final List<List<Node>> paths) {
        //base evaluation on place
        return ctlExpression.evaluate(place, paths);
    }

    /**
     * @param ctlExpression a {@link TransitionFormula} to evaluate
     * @param transition a {@link Transition} of a
     *      {@link io.dataspaceconnector.extension.petrinet.model.PetriNet}
     * @param paths possible pathes through the PetriNet
     * @return result of the evaluation of the ctlExpression
     */
    public static boolean evaluateTransition(final TransitionFormula ctlExpression,
                                             final Transition transition,
                                             final List<List<Node>> paths) {
        //base evaluation on transition
        return ctlExpression.evaluate(transition, paths);
    }

    /**
     * @param ctlExpression a {@link Formula} to evaluate
     * @param node a {@link Node} of a
     *      {@link io.dataspaceconnector.extension.petrinet.model.PetriNet}
     * @param paths possible pathes through the PetriNet
     * @return result of the evaluation of the ctlExpression
     *      (or false, if formula and node types don't match)
     */
    public static boolean evaluate(final Formula ctlExpression,
                                   final Node node,
                                   final List<List<Node>> paths) {
            if (ctlExpression instanceof StateFormula && node instanceof Place) {
                return evaluateNode((StateFormula) ctlExpression, (Place) node, paths);
            } else if (ctlExpression instanceof TransitionFormula && node instanceof Transition) {
                return evaluateTransition((TransitionFormula) ctlExpression,
                        (Transition) node, paths);
            } else {
                //cannot be evaluated
                return false;
            }
    }

}
