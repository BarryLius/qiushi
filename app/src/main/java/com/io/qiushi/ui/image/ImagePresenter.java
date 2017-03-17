package com.io.qiushi.ui.image;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.io.qiushi.api.ApiService;
import com.io.qiushi.bean.Image;
import com.io.qiushi.util.NetUtils;
import com.io.qiushi.util.NetworkUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mhl on 2017/3/15.
 */

public class ImagePresenter implements ImageContract.Presenter {
    private Context mContext;
    private ImageContract.View mView;

    public ImagePresenter(Context mContext, ImageContract.View mView) {
        this.mContext = mContext;
        this.mView = mView;
        mView.setPresenter(this);
    }

    @Override
    public void getData(@NonNull int page) {
        if (!NetUtils.isNetworkAvailable(mContext)) {
            mView.networkError();
            return;
        }

        if (page == 1) {
            mView.setLoading();
        }
        NetworkUtils.getInstance()
                .create(ApiService.class)
                .getImageData(page)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.e("图片json", ">>" + new Gson().toJson(response.body()));
                        if (response.isSuccessful()) {
                            List<Image> list = string2Object(response.body().toString());
                            mView.setData(list);
                        } else {
                            mView.serverError();
                        }
                        mView.setLoaded();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        mView.serverError();
                        mView.setLoaded();
                    }
                });
    }

    @Override
    public List<Image> string2Object(@NonNull String msg) {
        List<Image> list = new ArrayList<>();
        if (!TextUtils.isEmpty(msg)) {
            Document doc = Jsoup.parse(msg);
            Elements ele = doc.select("div.main").select("img");
            for (int i = 0; i < ele.size(); i++) {
//                if (i % 4 == 3) {
//                    Image image = new Image();
//                    image.setSrc(ele.get(i).attr("src"));
//                    list.add(image);
//                }
                if (!ele.get(i).attr("src").contains("1x1.trans.gif")) {
                    Image image = new Image();
                    image.setSrc(ele.get(i).attr("src"));
                    list.add(image);
                }
            }
        }
        return list;
    }
}
