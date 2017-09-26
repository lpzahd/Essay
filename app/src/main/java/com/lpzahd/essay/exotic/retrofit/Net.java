package com.lpzahd.essay.exotic.retrofit;

import com.lpzahd.base.NoInstance;
import com.lpzahd.essay.context.leisure.baidu.BaiduPic;


import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
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

    private Net() {}

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

    public interface BaiduImgApi {

        /**
         * 百度图片
         * @param word  查询关键字
         * @param count 每页查询数量
         * @param index 起始位置
         * @param gsm   随机数 Integer.toHexString(i * 30)
         */
        @GET("/search/avatarjson?tn=resultjsonavatarnew&ie=utf-8&cg=star&itg=0&z=0&fr=&width=&height=&lm=-1&ic=0&s=0")
        Observable<BaiduPic> searchImage(@Query("word") String word, @Query("rn") int count, @Query("pn") int index, @Query("gsm") String gsm);
    }
}
