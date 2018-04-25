package com.lpzahd.common.util.fresco;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.lpzahd.atool.enmu.ImageSource;
import com.lpzahd.Strings;
import com.lpzahd.atool.constant.Constance;
import com.lpzahd.base.NoInstance;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : SOURCE_MEMORY 仅支持base64编码的图片
 */
public class Frescoer extends NoInstance {

    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";

    public static final String SCHEME_FILE = "file";
    public static final String SCHEME_RES = "res";
    public static final String SCHEME_ASSET = "asset";


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
            case SCHEME_FILE:
                return ImageSource.SOURCE_FILE;
            case SCHEME_RES:
                return ImageSource.SOURCE_RES;
            case SCHEME_ASSET:
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
