package com.lpzahd.common.util.fresco;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.lpzahd.atool.enmu.Image;
import com.lpzahd.Strings;
import com.lpzahd.atool.constant.Constance;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class Frescoer {

    private Frescoer() {
        throw new AssertionError("No Frescoer instances for you!");
    }

    public static Uri res(@DrawableRes int resId) {
        return uri(String.valueOf(resId), Image.SOURCE_RES);
    }

    public static Uri uri(@NonNull String path, @Image.SOURCE int source) {
        Uri uri;
        switch (source) {
            case Image.SOURCE_NET:
                uri = Uri.parse(path);
                break;
            case Image.SOURCE_FILE:
                uri = Uri.parse(Strings.join("file://", Constance.AppPackageName, "/", path));
                break;
            case Image.SOURCE_RES:
                uri = Uri.parse(Strings.join("res://", Constance.AppPackageName, "/", path));
                break;
            case Image.SOURCE_ASSET:
                uri = Uri.parse(Strings.join("asset://", Constance.AppPackageName, "/", path));
                break;
            default:
                uri = Uri.parse(path);
                break;
        }
        return uri;
    }
}
