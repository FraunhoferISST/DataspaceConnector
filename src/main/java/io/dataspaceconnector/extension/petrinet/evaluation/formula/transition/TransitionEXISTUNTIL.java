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
package io.dataspaceconnector.extension.petrinet.evaluation.formula.transition;

import io.dataspaceconnector.extension.petrinet.model.Node;
import io.dataspaceconnector.extension.petrinet.model.Transition;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Evaluates to true, if a path exists, where parameter1 evaluates to true for every transition,
 * until parameter2 evaluates to true.
 */
@AllArgsConstructor
public class TransitionEXISTUNTIL implements TransitionFormula {
    /**
     * Evaluates to true for every transition on the path.
     */
    private TransitionFormula parameter1;

    /**
     * Path check ends when this parameter evaluates to true.
     */
    private TransitionFormula parameter2;

    /**
     * Evaluates to true, if a path exists, where parameter1 evaluates to true for every transition,
     * until parameter2 evaluates to true.
     * @param parameter1 Evaluates to true for every transition on the path.
     * @param parameter2 Path check ends when this parameter evaluates to true.
     * @return Transition representing the formula.
     */
    public static TransitionEXISTUNTIL transitionEXISTUNTIL(final TransitionFormula parameter1,
                                                            final TransitionFormula parameter2) {
        return new TransitionEXISTUNTIL(parameter1, parameter2);
    }

    // True if a path exists, where parameter1 is true on each transition of the path,
    // and parameter2 is true on the final transition of the path
    //TODO fix evaluation: use filtered paths
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        if (!(node instanceof Transition)) {
            return false;
        }

        check: for (final var path: paths) {
            int offset;
            if (!path.get(0).equals(node)) {
                continue;
            }
            if (path.size() % 2 == 1) {
                offset = 1;
            } else {
                offset = 2;
            }
            for (var i = 2; i < path.size() - offset; i += 2) {
                final var res1 = parameter1.evaluate(path.get(i), paths);
                final var res2 = parameter2.evaluate(path.get(i), paths);
                if (res2) {
                    return true;
                }
                if (!res1) {
                    continue check;
                }
            }
            if (parameter2.evaluate(path.get(path.size() - offset), paths)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String symbol() {
        return "EXIST_UNTIL";
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
