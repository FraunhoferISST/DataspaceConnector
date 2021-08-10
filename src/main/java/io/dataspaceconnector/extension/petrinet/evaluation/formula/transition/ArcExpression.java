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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Custom Expression to be evaluated on a
 * {@link io.dataspaceconnector.extension.petrinet.model.Transition}.
 */
@Getter
@AllArgsConstructor
public class ArcExpression {

    /**
     * Subexpression (function from
     * {@link io.dataspaceconnector.extension.petrinet.model.Transition} to boolean.
     */
    private ArcSubExpression subExpression;

    /**
     * Information about expression, used when writing Formulas to String.
     */
    private String message;

    /**
     * @param subExpression SubExpression to be evaluated
     * @param message Information about expression, used when writing Formulas to String
     * @return ArcExpression containing SubExpression and Message
     */
    public static ArcExpression arcExpression(final ArcSubExpression subExpression,
                                              final String message) {
        return new ArcExpression(subExpression, message);
    }
}
