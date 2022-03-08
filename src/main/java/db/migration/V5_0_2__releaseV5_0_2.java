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

import static db.migration.MigrationConstants.COLUMN_LENGTH_255;

/**
 * Performs migration operations for v5.1.0 in addition to the migration script.
 */
@SuppressWarnings("typename")
public class V5_0_2__releaseV5_0_2 extends BaseJavaMigration {

    /**
     * Performs the migration.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    @Override
    public void migrate(final Context ctx) throws Exception {
        migrateAgreements(ctx);
        migrateArtifacts(ctx);
        migrateContracts(ctx);
        migrateContractRules(ctx);
        migrateRepresentations(ctx);
        migrateResources(ctx);
    }

    /**
     * Migrates agreements. Changes the type of the column remote_id from URI to string.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    private void migrateAgreements(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.agreement");
        replacer.replace(ctx, "remote_id", COLUMN_LENGTH_255);
    }

    /**
     * Migrates artifacts. Changes the type of the columns remote_id and remote_address from
     * URI to string.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    private void migrateArtifacts(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.artifact");
        replacer.replace(ctx, "remote_address", COLUMN_LENGTH_255);
        replacer.replace(ctx, "remote_id", COLUMN_LENGTH_255);
    }

    /**
     * Migrates contracts. Changes the type of the columns remote_id, provider and consumer from
     * URI to string.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    private void migrateContracts(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.contract");
        replacer.replace(ctx, "consumer", COLUMN_LENGTH_255);
        replacer.replace(ctx, "provider", COLUMN_LENGTH_255);
        replacer.replace(ctx, "remote_id", COLUMN_LENGTH_255);
    }

    /**
     * Migrates contract rules. Changes the type of the column remote_id from URI to string.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    private void migrateContractRules(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.contractrule");
        replacer.replace(ctx, "remote_id", COLUMN_LENGTH_255);
    }

    /**
     * Migrates representations. Changes the type of the column remote_id from URI to string.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    private void migrateRepresentations(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.representation");
        replacer.replace(ctx, "remote_id", COLUMN_LENGTH_255);
    }

    /**
     * Migrates resources. Changes the type of the columns remote_id, sovereign, publisher, license
     * and endpoint_documentation from URI to string.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    private void migrateResources(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.resource");
        replacer.replace(ctx, "endpoint_documentation", COLUMN_LENGTH_255);
        replacer.replace(ctx, "licence", COLUMN_LENGTH_255);
        replacer.replace(ctx, "publisher", COLUMN_LENGTH_255);
        replacer.replace(ctx, "sovereign", COLUMN_LENGTH_255);
        replacer.replace(ctx, "remote_id", COLUMN_LENGTH_255);
    }
}
