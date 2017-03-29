package com.io.qiushi.ui.text;

import android.support.annotation.NonNull;

import com.io.qiushi.bean.Text;
import com.io.qiushi.ui.base.BasePresenter;
import com.io.qiushi.ui.base.BaseView;

import java.util.List;

/**
 * Created by mhl on 2017/3/29.
 */

public class TextContract {
    interface View extends BaseView<Presenter> {
        void setLoading();

        void setLoaded();

        void networkError();

        void serverError();

        void setData(List<Text> list);

        void noMoreData();

    }

    interface Presenter extends BasePresenter {
        void getData(@NonNull int page);

        List<Text> string2Object(@NonNull String msg);
    }
}
