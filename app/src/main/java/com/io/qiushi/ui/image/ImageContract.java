package com.io.qiushi.ui.image;

import android.support.annotation.NonNull;

import com.io.qiushi.bean.Image;
import com.io.qiushi.ui.base.BasePresenter;
import com.io.qiushi.ui.base.BaseView;

import java.util.List;

/**
 * Created by mhl on 2017/3/15.
 */

public interface ImageContract {

    interface View extends BaseView<Presenter> {
        void setLoading();

        void setLoaded();

        void networkError();

        void serverError();

        void setData(List<Image> list);
    }

    interface Presenter extends BasePresenter {
        void getData(@NonNull int page);

        List<Image> string2Object(@NonNull String msg);
    }

}
