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
package io.dataspaceconnector.service.resource.ids.builder;

import de.fraunhofer.iais.eis.Prohibition;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import org.springframework.stereotype.Component;

/**
 * Converts dsc rule to ids prohibition.
 */
@Component
public class IdsProhibitionBuilder extends IdsRuleBuilder<Prohibition> {
    IdsProhibitionBuilder(final SelfLinkHelper selfLinkHelper,
                          final DeserializationService deserializer) {
        super(selfLinkHelper, deserializer, Prohibition.class);
    }
}
