package com.lpzahd.essay.exotic.realm;

import android.app.Application;

import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.keeper.Keeper;
import com.lpzahd.atool.ui.L;
import com.lpzahd.essay.app.App;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class Realmer {

    private Realmer() {
        throw new AssertionError("No Realm instances for you!");
    }

    public static void init() {
        final Application app = App.getApp();
        Realm.init(app);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .directory(Keeper.getF().getScopeFile(Files.Scope.DATABASE))
                .name("lpzahd.realm")
                .schemaVersion(1L)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                        L.e(realm.toString() + oldVersion + newVersion);
                        if(oldVersion == 0 && newVersion == 1) {
                            RealmSchema schema = realm.getSchema();
                            schema.create("Collection")
                                    .addField("id", String.class, FieldAttribute.PRIMARY_KEY)
                                    .addField("spare", String.class)
                                    .addField("date", long.class)
                                    .addRealmObjectField("image",schema.get("Image"))
                                    .addField("originalPath", String.class)
                                    .addField("MD5", String.class)
                                    .addField("trans", boolean.class)
                                    .addField("count", int.class)
                                    .addField("tag", String.class)
                                    .addField("weight", int.class);
                        }
                    }
                })
                .build();
//        Realm.deleteRealm(config);
        Realm.setDefaultConfiguration(config);
    }
}
