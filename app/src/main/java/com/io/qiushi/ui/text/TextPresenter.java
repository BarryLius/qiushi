package com.io.qiushi.ui.text;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.io.qiushi.api.ApiService;
import com.io.qiushi.bean.Text;
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
 * Created by mhl on 2017/3/29.
 */

public class TextPresenter implements TextContract.Presenter {
    private Context mContext;
    private TextContract.View mView;

    public TextPresenter(Context context, TextContract.View view) {
        mContext = context;
        mView = view;
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

        Call<String> call = NetworkUtils.getInstance(ApiService.API_QIUMEIMEI_URL)
                .create(ApiService.class)
                .getTextData(page);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    List<Text> list = string2Object(response.body().toString());
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
    public List<Text> string2Object(@NonNull String msg) {
        List<Text> list = new ArrayList<>();
        if (!TextUtils.isEmpty(msg)) {
            Document doc = Jsoup.parse(msg);
            Elements el = doc.select("div.panel");
            for (int i = 0; i < el.size(); i++) {
                String title = el.select("div.top").get(i).select("h2").select("a").text();
                String time = el.select("div.top").get(i).select("time").text();
                String content = el.select("div.main").get(i).select("p").text();

                Text text = new Text();
                text.setTitle(title);
                text.setTime(time);
                text.setContent(content);
                list.add(text);
            }

        }
        return list;
    }
}
