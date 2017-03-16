package com.io.qiushi.ui.image;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.io.qiushi.R;
import com.io.qiushi.bean.Image;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageActivity extends AppCompatActivity implements ImageContract.View, SwipeRefreshLayout
        .OnRefreshListener {
    private static final String TAG = "ImageActivity";
    private final int RV_COLUMN = 2;

    private Context mContext;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.srl_refresh)
    SwipeRefreshLayout srlRefresh;
    @BindView(R.id.rv_data)
    RecyclerView rvData;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;

    ImageContract.Presenter mPresenter;
    private int page = 1;

    ImageAdapter2 adapter;
    GridLayoutManager gridLayoutManager;
    int lastVisibleItem;
    List<Image> tempList = new ArrayList<>();
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mContext = this;
        ButterKnife.bind(this);
        mPresenter = new ImagePresenter(mContext, this);
        initView();
        initData();
    }

    private void initView() {
        srlRefresh.setOnRefreshListener(this);
        gridLayoutManager = new GridLayoutManager(mContext, RV_COLUMN);
        rvData.setLayoutManager(gridLayoutManager);
        rvData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    toolbar.setTranslationZ(10f);
                } else {
                    toolbar.setTranslationZ(0f);
                }
            }
        });
        rvData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItem + 1) == adapter.getItemCount()) {
                    if (!isLoading) {
                        isLoading = true;
                        //load
                        adapter.startLoading();
                        Log.e(TAG, "loading...");
                        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                if (position == adapter.getLoadingItemPosition()) {
                                    return RV_COLUMN;
                                }
                                return 1;
                            }
                        });
                        //
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        isLoading = false;
                                        List<Image> list = new ArrayList<Image>();
                                        Image i = new Image();
                                        i.setSrc("https://ss0.bdstatic" +
                                                ".com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
                                        list.add(i);
                                        Image i2 = new Image();
                                        i2.setSrc("https://ss0.bdstatic" +
                                                ".com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
                                        list.add(i2);
                                        Image i3 = new Image();
                                        i3.setSrc("https://ss0.bdstatic" +
                                                ".com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
                                        list.add(i3);
                                        adapter.finishLoading();
                                        tempList.addAll(list);
                                        adapter.notifyItemRangeInserted(adapter.getItemCount(), list.size());
                                    }
                                });
                            }
                        }).start();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void initData() {
        adapter = new ImageAdapter2(mContext, null);
        rvData.setAdapter(adapter);
        mPresenter.getData(page);
    }

    @Override
    public void setPresenter(ImageContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setLoading() {
        srlRefresh.setRefreshing(true);
    }

    @Override
    public void setLoaded() {
        srlRefresh.setRefreshing(false);
    }

    @Override
    public void noNetwork() {
        Toast.makeText(mContext, "无网络", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setData(List<Image> list) {
        Log.e(TAG, ">>" + list.size());
        tempList.addAll(list);
        adapter = new ImageAdapter2(mContext, tempList);
        rvData.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        mPresenter.getData(page);
    }
}
