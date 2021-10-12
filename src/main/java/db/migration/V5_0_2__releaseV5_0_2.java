package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V5_0_2__releaseV5_0_2 extends BaseJavaMigration {
    @Override
    public void migrate(Context ctx) throws Exception {
        migrateAgreements(ctx);
        migrateArtifacts(ctx);
        migrateContracts(ctx);
        migrateContractRules(ctx);
        migrateRepresentations(ctx);
        migrateResources(ctx);
    }

    private void migrateAgreements(Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.agreement");
        replacer.replace(ctx, "remote_id", 255);
    }

    private void migrateArtifacts(Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.artifact");
        replacer.replace(ctx, "remote_address", 255);
        replacer.replace(ctx, "remote_id", 255);
    }

    private void migrateContracts(Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.contract");
        replacer.replace(ctx, "consumer", 255);
        replacer.replace(ctx, "provider", 255);
        replacer.replace(ctx, "remote_id", 255);
    }

    private void migrateContractRules(Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.contractrule");
        replacer.replace(ctx, "remote_id", 255);
    }

    private void migrateRepresentations(Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.representation");
        replacer.replace(ctx, "remote_id", 255);
    }

    private void migrateResources(Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.resource");
        replacer.replace(ctx, "endpoint_documentation", 255);
        replacer.replace(ctx, "licence", 255);
        replacer.replace(ctx, "publisher", 255);
        replacer.replace(ctx, "sovereign", 255);
        replacer.replace(ctx, "remote_id", 255);
    }
}
