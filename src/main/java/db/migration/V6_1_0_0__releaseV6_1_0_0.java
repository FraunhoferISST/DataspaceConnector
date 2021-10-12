package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V6_1_0_0__releaseV6_1_0_0 extends BaseJavaMigration {

    @Override
    public void migrate(Context ctx) throws Exception {
        migrateSubscriptions(ctx);
    }

    private void migrateSubscriptions(Context ctx) throws Exception {
        final var replacer = new ByteaToStringColumnReplacer("public.subscription");
        replacer.replace(ctx, "location", 2048);
        replacer.replace(ctx, "subscriber", 2048);
        replacer.replace(ctx, "target", 2048);
    }
}
