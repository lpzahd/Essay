package com.lpzahd.essay.exotic.realm;

import android.app.Application;

import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.keeper.Keeper;
import com.lpzahd.essay.app.App;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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
                .build();
        Realm.deleteRealm(config);
        Realm.setDefaultConfiguration(config);
    }
}
