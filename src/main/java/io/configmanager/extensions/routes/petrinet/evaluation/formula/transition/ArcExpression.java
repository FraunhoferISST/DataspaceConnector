/*
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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Custom Expression to be evaluated on a {@link io.configmanager.extensions.routes.petrinet.model.Transition}.
 */
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArcExpression {

    /**
     * Subexpression (function from {@link io.configmanager.extensions.routes.petrinet.model.Transition} to boolean.
     */
    ArcSubExpression subExpression;

    /**
     * Information message to return when subExpression is not fulfilled by a transition.
     */
    String message;

    public static ArcExpression arcExpression(final ArcSubExpression subExpression,
                                              final String message) {
        return new ArcExpression(subExpression, message);
    }
}
