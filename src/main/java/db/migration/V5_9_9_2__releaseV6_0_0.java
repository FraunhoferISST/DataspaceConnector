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

import java.sql.SQLException;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static db.migration.MigrationConstants.COLUMN_LENGTH_2048;

/**
 * Performs migration operations for v6.0.0 in addition to the migration script.
 */
@SuppressWarnings("typename")
public class V5_9_9_2__releaseV6_0_0 extends BaseJavaMigration {

    /**
     * Query for selecting username and password from the data table.
     */
    private static final String SELECT_AUTH_INFO_FROM_DATA = "SELECT id,username,password"
            + " FROM public.data"
            + " WHERE username IS NOT NULL"
            + " AND password IS NOT NULL";

    /**
     * Query for selecting the current maximum value for IDs of type Long/BigInt. This ID type
     * is only used in the data and authentication tables.
     */
    private static final String HIGHEST_CURRENT_ID_VALUE = "SELECT MAX(id) FROM"
            + " ("
            + " SELECT id FROM public.data"
            + " UNION"
            + " SELECT id FROM public.authentication"
            + " )"
            + " AS subquery";

    /**
     * Prepared query for inserting a new authentication.
     */
    private static final String INSERT_AUTH = "INSERT INTO public.authentication"
            + " (dtype, id, username, password)"
            + " VALUES ('BasicAuth', '%s', '%s', '%s')"
            + " RETURNING id";

    /**
     * Prepared query for setting the reference between data and authentication.
     */
    private static final String SET_AUTH_REFERENCE = "INSERT INTO public.data_authentication"
            + " (remote_data_id, authentication_id)"
            + " VALUES(%s, %s)";

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
        migrateCatalogs(ctx);
        migrateContracts(ctx);
        migrateContractRules(ctx);
        migrateRemoteData(ctx);
        migrateRepresentation(ctx);
        migrateResource(ctx);
    }

    /**
     * Migrates agreements. Changes the type of the column bootstrap_id from URI to string.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    private void migrateAgreements(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.agreement");
        replacer.replace(ctx, "bootstrap_id", COLUMN_LENGTH_2048);
    }

    /**
     * Migrates artifacts. Changes the type of the column bootstrap_id from URI to string.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    private void migrateArtifacts(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.artifact");
        replacer.replace(ctx, "bootstrap_id", COLUMN_LENGTH_2048);
    }

    /**
     * Migrates catalogs. Changes the type of the column bootstrap_id from URI to string.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    private void migrateCatalogs(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.catalog");
        replacer.replace(ctx, "bootstrap_id", COLUMN_LENGTH_2048);
    }

    /**
     * Migrates contracts. Changes the type of the column bootstrap_id from URI to string.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    private void migrateContracts(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.contract");
        replacer.replace(ctx, "bootstrap_id", COLUMN_LENGTH_2048);
    }

    /**
     * Migrates contract rules. Changes the type of the column bootstrap_id from URI to string.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    private void migrateContractRules(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.contractrule");
        replacer.replace(ctx, "bootstrap_id", COLUMN_LENGTH_2048);
    }

    /**
     * Migrates remote data. Exchanges the columns username and password for a column that links
     * to the authentication table.
     *
     * @param ctx The Flyway context.
     * @throws SQLException if any error occurs when performing database operations.
     */
    private void migrateRemoteData(final Context ctx) throws SQLException {
        replaceUsernameAndPasswordWithAuthentication(ctx);
    }

    /**
     * Migrates representations. Changes the type of the column bootstrap_id from URI to string.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    private void migrateRepresentation(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.representation");
        replacer.replace(ctx, "bootstrap_id", COLUMN_LENGTH_2048);
    }

    /**
     * Migrates resources. Changes the type of the column bootstrap_id from URI to string.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    private void migrateResource(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.resource");
        replacer.replace(ctx, "bootstrap_id", COLUMN_LENGTH_2048);
    }

    /**
     * Exchanges the columns username and password of the data table for a column that links to
     * the authentication table. Username and password are read from the data table and inserted
     * into the authentication table. Afterwards, the new authentication ID is linked to the
     * data entry where username and password were extracted.
     *
     * @param ctx The Flyway context.
     * @throws SQLException if any error occurs when performing database operations.
     */
    @SuppressFBWarnings(value={"SQL_INJECTION_JDBC", "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
            "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE",
            "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE"})
    private void replaceUsernameAndPasswordWithAuthentication(final Context ctx)
            throws SQLException {
        try (var select = ctx.getConnection().createStatement()) {
            try (var rows = select.executeQuery(SELECT_AUTH_INFO_FROM_DATA)) {
                while (rows.next()) {
                    try (var insert = select.getConnection().createStatement()) {
                        final var username = rows.getString(2);
                        final var password = rows.getString(3);

                        try (var idSelect = ctx.getConnection().createStatement()) {
                            try (var idRows = idSelect
                                    .executeQuery(HIGHEST_CURRENT_ID_VALUE)) {
                                if (idRows.next()) {
                                    var id = idRows.getObject(1, Long.class) + 1;
                                    try (var result = insert.executeQuery(
                                            String.format(INSERT_AUTH, id, username, password))) {
                                        if (result.next()) {
                                            final var dataId = rows.getObject(1, Long.class);
                                            final var authId = result.getObject(1, Long.class);
                                            insert.execute(String
                                                    .format(SET_AUTH_REFERENCE, dataId, authId));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
