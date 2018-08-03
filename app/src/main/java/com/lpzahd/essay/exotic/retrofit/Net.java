package com.lpzahd.essay.exotic.retrofit;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.lpzahd.essay.context.instinct.yiyibox.YiyiBox;
import com.lpzahd.essay.context.leisure.baidu.BaiduPic;
import com.lpzahd.essay.context.pure.bilibili.BiliBiliCos;
import com.lpzahd.essay.context.pure.bx6644.Bx6644;
import com.lpzahd.essay.context.pure.bx6644.BxPhotos;
import com.lpzahd.essay.context.turing.turing123.Turing123;
import com.lpzahd.essay.tool.Convert;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class Net {

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    private static CustomGsonConverterFactory gsonConverterFactory = CustomGsonConverterFactory.create();

//    private static GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJava2CallAdapterFactory.create();

    private static Net sNet;

    public static Net get() {
        return sNet == null ? (sNet = new Net()) : sNet;
    }

    private Net() {
    }

    public Retrofit it(String url) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(url)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();
    }

    public Observable<BaiduPic> baiduImg(String word, int page, int count) {
        return it("http://image.baidu.com")
                .create(BaiduImgApi.class)
                .searchImage(word, count, page * count, Integer.toHexString(page * count))
                .subscribeOn(Schedulers.io());
    }

    interface BaiduImgApi {

        /**
         * 百度图片
         *
         * @param word  查询关键字
         * @param count 每页查询数量
         * @param index 起始位置
         * @param gsm   随机数 Integer.toHexString(i * 30)
         */
        @GET("/search/avatarjson?tn=resultjsonavatarnew&ie=utf-8&cg=star&itg=0&z=0&fr=&width=&height=&lm=-1&ic=0&s=0")
        Observable<BaiduPic> searchImage(@Query("word") String word, @Query("rn") int count, @Query("pn") int index, @Query("gsm") String gsm);
    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Observable<Turing123> turing(String message) {
        return it("http://www.tuling123.com")
                .create(Turing123Api.class)
                .sendMessage(RequestBody.create(JSON, Convert.toJson(new TuringParam(message))));
    }

    static class TuringParam {
        public String key = "a22cc6ebcb8d48a0a7ad46d189cad393";
        public String userid = "3f9a72856c18dc2d";
        public String info;

        TuringParam(String info) {
            this.info = info;
        }
    }

    interface Turing123Api {

        /**
         * turing123 请求接口
         *
         * @param message 消息内容
         */
        @POST("/openapi/api")
        @Headers({"Content-Type: application/json","Accept: application/json"})
        Observable<Turing123> sendMessage(@Body RequestBody message);
    }

    public static final String BOX_HOST = "http://www.sesehezi.com";

    public Observable<YiyiBox> yiyiBoxHomeImg(int page) {
        return it(BOX_HOST)
                .create(YiyiBoxApi.class)
                .searchImage(page);
    }

    public Observable<YiyiBox> yiyiBoxHomeVideo(int page) {
        return it(BOX_HOST)
                .create(YiyiBoxApi2.class)
                .searchVideo(page)
                .subscribeOn(Schedulers.io());
    }

    public Observable<YiyiBox> yiyiBoxImg(int page) {
        return it(BOX_HOST)
                .create(YiyiBoxImgApi.class)
                .searchImage(page);
    }

    public Observable<YiyiBox> yiyiBoxVideo(int page) {
        return it(BOX_HOST)
                .create(YiyiBoxVideoApi.class)
                .searchVideo(page);
    }

    public Observable<YiyiBox> yiyiBoxTopImg(int page) {
        return it(BOX_HOST)
                .create(YiyiBoxImgTopApi.class)
                .searchImage(page);
    }

    public Observable<YiyiBox> yiyiBoxTopVideo(int page) {
        return it(BOX_HOST)
                .create(YiyiBoxVideoTopApi.class)
                .searchVideo(page);
    }

    interface YiyiBoxApi {

        /**
         * yiyibox图片
         *
         * @param page  页码(从第一页开始)
         */
        @GET("/api/v1.1/home")
        Observable<YiyiBox> searchImage(@Query("page") int page);
    }

    interface YiyiBoxApi2 {

        /**
         * yiyibox图片
         *
         * @param page  页码(从第一页开始)
         */
        @GET("/api/v1.2/home")
        Observable<YiyiBox> searchVideo(@Query("page") int page);
    }

    interface YiyiBoxImgApi {

        /**
         * yiyibox图片
         *
         * @param page  页码(从第一页开始)
         */
        @GET("/api/v1.1/")
        Observable<YiyiBox> searchImage(@Query("page") int page);
    }

    interface YiyiBoxVideoApi {

        /**
         * yiyibox图片
         *
         * @param page  页码(从第一页开始)
         */
        @GET("/api/v1.2/")
        Observable<YiyiBox> searchVideo(@Query("page") int page);
    }

    interface YiyiBoxImgTopApi {

        /**
         * yiyibox图片
         *
         * @param page  页码(从第一页开始)
         */
        @GET("/api/v1.1/top")
        Observable<YiyiBox> searchImage(@Query("page") int page);
    }

    interface YiyiBoxVideoTopApi {

        /**
         * yiyibox图片
         *
         * @param page  页码(从第一页开始)
         */
        @GET("/api/v1.2/top")
        Observable<YiyiBox> searchVideo(@Query("page") int page);
    }


    /**
     * @param page 页码从第二页开始，第一页的时候不知道返回的是啥
     */
    public Observable<Bx6644> pureList(int page) {
        return it("http:/www.8888ez.com")
                .create(PureListApi.class)
                .pureList("http://www.8888ez.com/html/artlist/qingchunweimei/26_" + page + ".json");
    }

    public interface PureListApi {

        /**
         * 6644bx 获取每页数据
         * @param url 详细接口地址 （http://www.6666bx.com/html/artlist/qingchunweimei/26_59.json）
         */
        @GET
        Observable<Bx6644> pureList(@Url String url);

    }

    /**
     * @param id 图片id
     */
    public Observable<BxPhotos> purePhotos(String id) {
            return it("http://www.8888ez.com")
                .create(PurePhotosApi.class)
                .purePhotos("http://www.8888ez.com/html/artdata/" + id + ".json");
    }

    public interface PurePhotosApi {

        /**
         * 6644bx 获取每页数据
         * @param url 详细接口地址 （http://www.6666bx.com/html/artlist/qingchunweimei/26_59.json）
         */
        @GET
        Observable<BxPhotos> purePhotos(@Url String url);

    }

    /**
     * bilibili cos 图片查询接口
     */
    public Observable<BiliBiliCos> searchCos(String type, int page, int size) {
        return it("http://api.vc.bilibili.com")
                .create(BiliPhotosApi.class)
                .searchCos(type, page, size);
    }


    public interface BiliPhotosApi {

        String HOT = "hot";
        String NEW = "new";

        /**
         * bilibili cos 图片接口
         *
         * http://api.vc.bilibili.com/link_draw/v2/Photo/list?type=hot&category=cos&page_num=0&page_size=20
         *
         * @param page  页码(从第0页开始)
         */
        @GET("/link_draw/v2/Photo/list?type=hot&category=cos")
        Observable<BiliBiliCos> searchCos(@Query("type") String type, @Query("page_num") int page, @Query("page_size") int size);

    }

    public static class CustomGsonConverterFactory extends Converter.Factory{
        private final Gson gson;

        public static CustomGsonConverterFactory create(){
            return create(new Gson());
        }


        private CustomGsonConverterFactory(Gson gson){
            this.gson = gson;
        }

        public static CustomGsonConverterFactory create(Gson gson) {
            if (gson == null) throw new NullPointerException("gson == null");
            return new CustomGsonConverterFactory(gson);
        }


        @Nullable
        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
            return new CustomGsonRequestBodyConverter<>(gson, adapter);
        }

        @Nullable
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
            return new CustomGsonResponseBodyConverter<>(gson, adapter);
        }
    }

    final static class CustomGsonRequestBodyConverter<T> implements Converter<T, RequestBody> {

        private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
        private static final Charset UTF_8 = Charset.forName("UTF-8");

        private final Gson gson;
        private final TypeAdapter<T> adapter;

        CustomGsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override public RequestBody convert(T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, value);
            jsonWriter.close();
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }

    final static class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final TypeAdapter<T> adapter;

        CustomGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override public T convert(ResponseBody value) throws IOException {
            JsonReader jsonReader = gson.newJsonReader(value.charStream());
            jsonReader.setLenient(true);
            try {
                return adapter.read(jsonReader);
            } finally {
                value.close();
            }
        }
    }
}
