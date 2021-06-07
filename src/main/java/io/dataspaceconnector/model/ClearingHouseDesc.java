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
package io.dataspaceconnector.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.Objects;

/**
 * Describing the clearing house's properties.
 */
@Data
@NoArgsConstructor
public class ClearingHouseDesc extends AbstractDescription<ClearingHouse> {

    /**
     * The access url of the clearing house.
     */
    private URI accessUrl;

    /**
     * The title of the clearing house.
     */
    private String title;

    /**
     * The status of registration.
     */
    private RegistrationStatus registrationStatus;

    /**
     * @param o The reference object with which to compare.
     * @return True, if this object is the same as the obj argument.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ClearingHouseDesc that = (ClearingHouseDesc) o;
        return Objects.equals(accessUrl, that.accessUrl)
                && Objects.equals(title, that.title)
                && registrationStatus == that.registrationStatus;
    }

    /**
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accessUrl, title, registrationStatus);
    }
}
