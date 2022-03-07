/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.model.pattern;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dataspaceconnector.model.named.NamedDescription;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class for inputs of a policy pattern.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeletionDesc.class, names = {"USAGE_UNTIL_DELETION"}),
        @JsonSubTypes.Type(value = DurationDesc.class, names = {"DURATION_USAGE"}),
        @JsonSubTypes.Type(value = IntervalDesc.class, names = {"USAGE_DURING_INTERVAL"}),
        @JsonSubTypes.Type(value = LoggingDesc.class, names = {"USAGE_LOGGING"}),
        @JsonSubTypes.Type(value = NotificationDesc.class, names = {"USAGE_NOTIFICATION"}),
        @JsonSubTypes.Type(value = PermissionDesc.class, names = {"PROVIDE_ACCESS"}),
        @JsonSubTypes.Type(value = ProhibitionDesc.class, names = {"PROHIBIT_ACCESS"}),
        @JsonSubTypes.Type(value = ConnectorRestrictionDesc.class,
                names = {"CONNECTOR_RESTRICTED_USAGE"}),
        @JsonSubTypes.Type(value = UsageNumberDesc.class, names = {"N_TIMES_USAGE"}),
        @JsonSubTypes.Type(value = SecurityRestrictionDesc.class,
                names = {"SECURITY_PROFILE_RESTRICTED_USAGE"})}
)
public class PatternDesc extends NamedDescription {
}
