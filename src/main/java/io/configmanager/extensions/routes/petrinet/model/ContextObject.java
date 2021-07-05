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
package io.configmanager.extensions.routes.petrinet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

/**
 * Context of a transition (used for WFDU nets).
 */
@Getter
@Setter
@AllArgsConstructor
public class ContextObject {
    private Set<String> context;
    private Set<String> read;
    private Set<String> write;
    private Set<String> erase;
    private TransType type;

    public ContextObject deepCopy() {
        return new ContextObject(context, read, write, erase, type);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var that = (ContextObject) o;

        return Objects.equals(context, that.context)
                && Objects.equals(read, that.read)
                && Objects.equals(write, that.write)
                && Objects.equals(erase, that.erase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(new ArrayList<>(context), read, write, erase);
    }

    /**
     * Transition types (are they apps or control transitions for the petrinet?),
     * only APP transitions have to be unfolded for parallel checks.
     */
    public enum TransType {
        APP,
        CONTROL
    }
}
