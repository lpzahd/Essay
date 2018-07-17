package com.lpzahd.essay.exotic.realm;

import android.app.Application;

import com.lpzahd.Objects;
import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.keeper.Keeper;
import com.lpzahd.base.NoInstance;

import java.io.File;

import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class Realmer extends NoInstance {

    private static final String REALM_DB_NAME = "lpzahd.realm";

    public static void init(Application app) {
        Realm.init(app);
        RealmConfiguration config =
                getConfigByEnv(Keeper.getF().getScopeFile(Files.Scope.DATABASE));

//        Realm.deleteRealm(config);
        Realm.setDefaultConfiguration(config);
    }

    private static RealmConfiguration getConfigByEnv(File directory) {
        return new RealmConfiguration.Builder()
                .directory(directory)
                .name(REALM_DB_NAME)
                .schemaVersion(1L)
                .migration((realm, oldVersion, newVersion) -> {
                    if (oldVersion == 0 && newVersion == 1) {
                        RealmSchema schema = realm.getSchema();
                        RealmObjectSchema imageSchema = schema.get("Image");
                        Objects.requireNonNull(imageSchema);

                        schema.create("Collection")
                                .addField("id", String.class, FieldAttribute.PRIMARY_KEY)
                                .addField("spare", String.class)
                                .addField("date", long.class)
                                .addRealmObjectField("image", imageSchema)
                                .addField("originalPath", String.class)
                                .addField("MD5", String.class)
                                .addField("trans", boolean.class)
                                .addField("count", int.class)
                                .addField("tag", String.class)
                                .addField("weight", int.class);
                    }
                })
                .build();
    }

    public static void close(Realm realm) {
        if (realm != null && !realm.isClosed()) realm.close();
    }

}
