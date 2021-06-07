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
import java.util.List;
import java.util.Objects;

/**
 * Describes a app store's properties.
 */
@Data
@NoArgsConstructor
public class AppStoreDesc extends AbstractDescription<AppStore> {

    /**
     * The access url of the app store.
     */
    private URI accessUrl;

    /**
     * The title of the app store.
     */
    private String title;

    /**
     * The registration status.
     */
    private RegistrationStatus registrationStatus;

    /**
     * The list of apps.
     */
    private List<App> appList;

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
        AppStoreDesc that = (AppStoreDesc) o;
        return Objects.equals(accessUrl, that.accessUrl)
                && Objects.equals(title, that.title)
                && registrationStatus == that.registrationStatus
                && Objects.equals(appList, that.appList);
    }

    /**
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accessUrl, title, registrationStatus, appList);
    }
}
