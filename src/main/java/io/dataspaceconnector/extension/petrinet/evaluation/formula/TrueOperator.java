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

import java.util.List;

/**
 * TrueOperator evaluates to True every time.
 */

public class TrueOperator implements StateFormula, TransitionFormula {
    /**
     * TrueOperator evaluates to True every time.
     * @return The TrueOperator.
     */
    public static TrueOperator trueOperator() {
        return new TrueOperator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String symbol() {
        return "TrueOperator";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String writeFormula() {
        return symbol();
    }
}
