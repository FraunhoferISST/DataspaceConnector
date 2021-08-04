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

import io.dataspaceconnector.extension.petrinet.model.Transition;

/**
 * Interface describing ArcSubExpressions, can be used as lambda: {@link Transition} -> boolean.
 */
@FunctionalInterface
public interface ArcSubExpression {
    /**
     * Evaluates a arc transition.
     * @param transition The arc transition to be evaluated.
     * @return True if evaluation passed.
     */
    boolean evaluate(Transition transition);
}
