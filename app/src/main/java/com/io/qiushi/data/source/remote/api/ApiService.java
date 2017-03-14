package com.io.qiushi.data.source.remote.api;

import android.support.annotation.NonNull;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by liuwei on 17/3/14.
 */

public interface ApiService {
    //
    public static final String API_URL = "http://www.qiumeimei.com/";

    /**
     * 获取[图片]数据
     *
     * @param page 页码
     * @return
     */
    @GET("page/{page}")
    Call<String> getImageData(@Path("page") @NonNull Integer page);

    /**
     * 获取[段子]数据
     *
     * @param page 页码
     * @return
     */
    @GET("text/page/{page}")
    Call<String> getTextData(@Path("page") @NonNull Integer page);
}
