package com.lpzahd.essay.exotic.fresco;

import android.content.Context;
import android.support.annotation.NonNull;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.lpzahd.Objects;
import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.keeper.Keeper;

import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author : Lpzahd
 * Date : 十月
 * Desction : (•ิ_•ิ)
 */
public class FrescoInit {

    private static FrescoInit sFrescoInit;

    private OkHttpClient mOkHttpClient;
    private RefererInterceptor mRefererInterceptor;

    private int initCount;

    private FrescoInit() {
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(mRefererInterceptor = new RefererInterceptor())
                .build();
    }

    public static FrescoInit get() {
        return Objects.isNull(sFrescoInit) ? (sFrescoInit = new FrescoInit()) : sFrescoInit;
    }

    public void init(Context context) {
        if(initCount > 0)
            throw new IllegalStateException("只能初始化一次");

        initCount++;
        final Context app = context.getApplicationContext();

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(app)
                .setBaseDirectoryPath(Keeper.getF().getScopeFile(Files.Scope.FRESCO))
                .setMaxCacheSize(100 * ByteConstants.MB)
                .build();

        ImagePipelineConfig pipelineConfig = OkHttpImagePipelineConfigFactory
                .newBuilder(app, mOkHttpClient)
                .setDownsampleEnabled(true)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();

        Fresco.initialize(app, pipelineConfig);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public void changeReferer(@NonNull String referer) {
        mRefererInterceptor.setReferer(referer);
    }

    public void removeReferer() {
        mRefererInterceptor.setReferer(null);
    }

    private static class RefererInterceptor implements Interceptor {

        private String referer;

        public void setReferer(String referer) {
            this.referer = referer;
        }

        public String getReferer() {
            return referer;
        }

        public RefererInterceptor() {

        }

        public RefererInterceptor(String referer) {
            this.referer = referer;
        }


        @Override
        public Response intercept(Chain chain) throws IOException {
            if(Objects.isNull(referer))
                return chain.proceed(chain.request());

            Request request = chain.request().newBuilder()
                    .addHeader("referer", URLEncoder.encode(referer, "utf-8"))
                    .build();
            return chain.proceed(request);
        }
    }

}
