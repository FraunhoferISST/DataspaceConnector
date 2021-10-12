package db.migration;

import java.sql.SQLException;
import java.util.UUID;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V6_0_0_1__releaseV6_0_0_1 extends BaseJavaMigration {

    @Override
    public void migrate(final Context ctx) throws Exception {
        migrateAgreements(ctx);
        migrateArtifacts(ctx);
        migrateCatalogs(ctx);
        migrateContracts(ctx);
        migrateContractRules(ctx);
        migrateData(ctx);
        migrateRepresentation(ctx);
        migrateResource(ctx);
    }

    private void migrateAgreements(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.agreement");
        replacer.replace(ctx, "bootstrap_id", 2048);
    }

    private void migrateArtifacts(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.artifact");
        replacer.replace(ctx, "bootstrap_id", 2048);
    }

    private void migrateCatalogs(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.catalog");
        replacer.replace(ctx, "bootstrap_id", 2048);
    }

    private void migrateContracts(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.contract");
        replacer.replace(ctx, "bootstrap_id", 2048);
    }

    private void migrateContractRules(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.contractrule");
        replacer.replace(ctx, "bootstrap_id", 2048);
    }

    private void migrateData(final Context ctx) throws SQLException {
        replaceUsernameAndPasswordWithAuthentication(ctx);
    }

    private void migrateRepresentation(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.representation");
        replacer.replace(ctx, "bootstrap_id", 2048);
    }

    private void migrateResource(final Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.resource");
        replacer.replace(ctx, "bootstrap_id", 2048);
    }

    private void replaceUsernameAndPasswordWithAuthentication(final Context ctx)
            throws SQLException {
        try(final var select = ctx.getConnection().createStatement()) {
            try(final var rows = select.executeQuery("SELECT id,username,password FROM public.data WHERE username<>NULL AND password <> NULL")) {
                while(rows.next()) {
                    try(final var insert = select.getConnection().createStatement()) {
                        try(final var result = insert.executeQuery("INSERT INTO public.authentication (dtype, username, password) VALUES (BasicAuth, + " + rows.getString(2)  + "," + rows.getString(3) + " ) RETURNING id")) {
                            insert.execute(
                                    "INSERT INTO public.data_authentication (remote_data_id, authentication_id) VALUES("
                                    + rows.getObject(1, UUID.class) + "," + result.getObject(1,
                                                                                             UUID.class)
                                    + ")");
                        }
                    }
                }
            }
        }
    }
}
