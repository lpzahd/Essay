package com.lpzahd.common.util.fresco;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.lpzahd.atool.enmu.ImageSource;
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
        return uri(String.valueOf(resId), ImageSource.SOURCE_RES);
    }

    public static Uri uri(@NonNull String path, @ImageSource.SOURCE int source) {
        Uri uri = null;
        switch (source) {
            case ImageSource.SOURCE_NET:
                uri = Uri.parse(path);
                break;
            case ImageSource.SOURCE_FILE:
                uri = Uri.parse(Strings.join("file://", Constance.AppPackageName, "/", path));
                break;
            case ImageSource.SOURCE_RES:
                uri = Uri.parse(Strings.join("res://", Constance.AppPackageName, "/", path));
                break;
            case ImageSource.SOURCE_ASSET:
                uri = Uri.parse(Strings.join("asset://", Constance.AppPackageName, "/", path));
                break;
        }
        return uri;
    }

    public static int parseImageSource(@NonNull Uri uri) {
        switch (uri.getScheme()) {
            case "file":
                return ImageSource.SOURCE_FILE;
            case "res":
                return ImageSource.SOURCE_RES;
            case "asset":
                return ImageSource.SOURCE_ASSET;
             default:
                 return ImageSource.SOURCE_NET;
        }
    }

    public static String parseUri(@NonNull Uri uri) {
        return parseUri(uri, parseImageSource(uri));
    }

    public static String parseUri(@NonNull Uri uri, @ImageSource.SOURCE int source) {
        switch (source) {
            case ImageSource.SOURCE_FILE:
            case ImageSource.SOURCE_RES:
            case ImageSource.SOURCE_ASSET:
                return uri.getPath();
            case ImageSource.SOURCE_NET:
                return uri.toString();
        }
        return null;
    }
}
