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
import java.util.regex.Pattern;

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
        //TODO need add cancel call
        Call<String> call = NetworkUtils.getInstance()
                .create(ApiService.class)
                .getImageData(page);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("图片json", ">>" + new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    List<Image> list = string2Object(response.body().toString());
                    mView.setData(list);
                } else if (response.code() == 404) {
                    mView.noMoreData();
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
        String pattern = "(http|htttps)://wx[0-9a-zA-Z].sinaimg.cn/mw[0-9]{3}/[0-9a-zA-Z]{32}.(jpg|gif)";

        List<Image> list = new ArrayList<>();
        if (!TextUtils.isEmpty(msg)) {
            Document doc = Jsoup.parse(msg);
//            //标题
//            Elements eleTitleText = doc.select("div.top").select("h2").select("a");
//            //图片
//            Elements eleImageSrc = doc.select("div.main").select("img");
//
//            for (int i = 0; i < eleImageSrc.size(); i++) {
//                String src = eleImageSrc.get(i).attr("src");
//                boolean isMatch = Pattern.matches(pattern, src);
//                if (isMatch) {
//                    Image image = new Image();
//                    image.setSrc(src);
//                    list.add(image);
//                }
//            }
            Elements el = doc.select("div.panel");
            for (int i = 0; i < el.size(); i++) {
                //标题
                String title = el.get(i).select("div.top").select("h2").select("a").text();
                //图片list
                Elements srcList = el.get(i).select("div.main").select("img");

                for (int j = 0; j < srcList.size(); j++) {
                    boolean isMatch = Pattern.matches(pattern, srcList.get(j).attr("src"));
                    if (isMatch) {
                        Image a = new Image();
                        a.setTitle(title);
                        a.setSrc(srcList.get(j).attr("src"));
                        list.add(a);
                    }
                }
            }

        }
        return list;
    }
}
