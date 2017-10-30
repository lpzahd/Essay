package com.lpzahd.essay.exotic.retrofit;

import com.lpzahd.essay.context.instinct.yiyibox.YiyiBox;
import com.lpzahd.essay.context.leisure.baidu.BaiduPic;
import com.lpzahd.essay.context.turing.turing123.Turing123;
import com.lpzahd.essay.tool.Convert;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class Net {

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
    private static GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create();
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
        Observable<Turing123> sendMessage(@Body RequestBody message);
    }

    public Observable<YiyiBox> yiyiBoxImg(int page) {
        return it("http:/www.yiyibox.com")
                .create(YiyiBoxApi.class)
                .searchImage(page);
    }

    public Observable<YiyiBox> yiyiBoxImg2(int page) {
        return it("http:/www.yiyibox.com")
                .create(YiyiBoxApi2.class)
                .searchImage(page)
                .subscribeOn(Schedulers.io());
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
        Observable<YiyiBox> searchImage(@Query("page") int page);
    }
}
