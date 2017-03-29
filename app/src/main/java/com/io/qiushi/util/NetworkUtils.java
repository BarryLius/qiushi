package com.io.qiushi.util;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by liuwei on 17/3/14.
 */

public class NetworkUtils {
    private static NetworkUtils instance;
    private Retrofit mRetrofit;

    public NetworkUtils(String url) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static NetworkUtils getInstance(String url) {
        if (instance == null) {
            synchronized (NetworkUtils.class) {
                if (instance == null) {
                    instance = new NetworkUtils(url);
                }
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }
}
