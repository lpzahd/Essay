package com.lpzahd.essay.exotic.fresco;

import android.content.Context;
import android.net.Uri;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.logging.FLog;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher;
import com.facebook.imagepipeline.common.BytesRange;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.producers.BaseNetworkFetcher;
import com.facebook.imagepipeline.producers.BaseProducerContextCallbacks;
import com.facebook.imagepipeline.producers.Consumer;
import com.facebook.imagepipeline.producers.FetchState;
import com.facebook.imagepipeline.producers.NetworkFetcher;
import com.facebook.imagepipeline.producers.ProducerContext;
import com.lpzahd.Objects;
import com.lpzahd.atool.keeper.Files;
import com.lpzahd.atool.keeper.Keeper;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Author : Lpzahd
 * Date : 十月
 * Desction : (•ิ_•ิ)
 */
public class FrescoInit {

    private static FrescoInit sFrescoInit;

    private StrategyImageFetcher mFetcher;

    private OkHttpNetworkFetcher mOkHttpNetworkFetcher;
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
                .setMaxCacheSize(500 * ByteConstants.MB)
                .build();

        mFetcher = new StrategyImageFetcher(mOkHttpNetworkFetcher = new OkHttpNetworkFetcher(mOkHttpClient));
        ImagePipelineConfig pipelineConfig = ImagePipelineConfig.newBuilder(context)
                .setNetworkFetcher(mFetcher)
                .setDownsampleEnabled(true)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();

        Fresco.initialize(app, pipelineConfig);
    }

    /**
     * 还原okhttp请求
     */
    public void resetOkHttpNetworkFetcher() {
        mFetcher.setFetcher(mOkHttpNetworkFetcher);
    }

    public void setFetcher(NetworkFetcher<OkHttpNetworkFetcher.OkHttpNetworkFetchState> fetcher) {
        mFetcher.setFetcher(fetcher);
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

    public static class StrategyImageFetcher
            implements NetworkFetcher<OkHttpNetworkFetcher.OkHttpNetworkFetchState> {

        private NetworkFetcher<OkHttpNetworkFetcher.OkHttpNetworkFetchState> fetcher;

        void setFetcher(NetworkFetcher<OkHttpNetworkFetcher.OkHttpNetworkFetchState> fetcher) {
            this.fetcher = fetcher;
        }

        StrategyImageFetcher(NetworkFetcher<OkHttpNetworkFetcher.OkHttpNetworkFetchState> fetcher) {
            this.fetcher = fetcher;
        }

        @Override
        public OkHttpNetworkFetcher.OkHttpNetworkFetchState createFetchState(Consumer<EncodedImage> consumer, ProducerContext producerContext) {
            return fetcher.createFetchState(consumer, producerContext);
        }

        @Override
        public void fetch(OkHttpNetworkFetcher.OkHttpNetworkFetchState fetchState, Callback callback) {
            fetcher.fetch(fetchState, callback);
        }

        @Override
        public boolean shouldPropagate(OkHttpNetworkFetcher.OkHttpNetworkFetchState fetchState) {
            return fetcher.shouldPropagate(fetchState);
        }

        @Override
        public void onFetchCompletion(OkHttpNetworkFetcher.OkHttpNetworkFetchState fetchState, int byteSize) {
            fetcher.onFetchCompletion(fetchState, byteSize);
        }

        @Nullable
        @Override
        public Map<String, String> getExtraMap(OkHttpNetworkFetcher.OkHttpNetworkFetchState fetchState, int byteSize) {
            return fetcher.getExtraMap(fetchState, byteSize);
        }
    }


}
