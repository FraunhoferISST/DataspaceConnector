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
package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import static db.migration.MigrationConstants.COLUMN_LENGTH_2048;

/**
 * Performs migration operations for v6.1.0 in addition to the migration script.
 */
@SuppressWarnings("typename")
public class V6_0_9__releaseV6_1_0 extends BaseJavaMigration {

    /**
     * Performs the migration.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    @Override
    public void migrate(final Context ctx) throws Exception {
        migrateSubscriptions(ctx);
    }

    /**
     * Migrates agreements. Changes the type of the columns location, subscriber and target from
     * URI to string.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    private void migrateSubscriptions(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.subscription");
        replacer.replace(ctx, "location", COLUMN_LENGTH_2048);
        replacer.replace(ctx, "subscriber", COLUMN_LENGTH_2048);
        replacer.replace(ctx, "target", COLUMN_LENGTH_2048);
    }
}
